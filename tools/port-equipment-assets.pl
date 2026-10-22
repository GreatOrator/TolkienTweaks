#!/usr/bin/perl
# Build the 1.21.4 equipment assets for the three armour sets.
#
# 1.21.4 renders armour from assets/<ns>/equipment/<set>.json, whose layer textures live at
# textures/entity/equipment/<layer>/<texture>.png. The old files were the 1.21.1 layout:
#   textures/models/armor/<set>_layer_1.png   128x64, a 2x nearest-neighbour blow-up of 64x32
#   textures/models/armor/<set>_layer_2.png   128x64, same, for leggings
#   textures/entity/horse/armor/<set>.png     64x64, already the horse_body format
# so the humanoid layers are halved and the horse body is copied. Nothing is drawn here: every
# pixel comes from existing art.
use strict;
use warnings;
use Compress::Zlib qw(compress uncompress);
use Compress::Raw::Zlib qw(crc32);

my $root = shift || 'src/main/resources/assets/tolkienmobs';
my @sets = qw(mithril morguliron ammolite);

sub read_png {
    my ($file) = @_;
    open my $fh, '<:raw', $file or die "$file: $!";
    local $/;
    my $data = <$fh>;
    close $fh;

    my $pos = 8;
    my ($w, $h, $bitDepth, $colorType);
    my $idat = '';
    while ($pos < length $data) {
        my ($len, $type) = unpack 'Na4', substr($data, $pos, 8);
        $pos += 8;
        my $body = substr($data, $pos, $len);
        $pos += $len + 4;
        if ($type eq 'IHDR') { ($w, $h, $bitDepth, $colorType) = unpack 'NNCC', $body; }
        elsif ($type eq 'IDAT') { $idat .= $body; }
        elsif ($type eq 'IEND') { last; }
    }
    die "$file: only 8-bit RGBA PNGs are supported\n" unless $bitDepth == 8 && $colorType == 6;

    my $raw = uncompress($idat);
    die "$file: cannot inflate\n" unless defined $raw;

    my (@rows, $prev, $off);
    $prev = "\0" x ($w * 4);
    $off = 0;
    for my $y (0 .. $h - 1) {
        my $filter = ord substr($raw, $off, 1);
        $off++;
        my @px = unpack 'C*', substr($raw, $off, $w * 4);
        $off += $w * 4;
        my @up = unpack 'C*', $prev;
        if ($filter == 1) { for my $i (4 .. $#px) { $px[$i] = ($px[$i] + $px[$i - 4]) & 255; } }
        elsif ($filter == 2) { for my $i (0 .. $#px) { $px[$i] = ($px[$i] + $up[$i]) & 255; } }
        elsif ($filter == 3) {
            for my $i (0 .. $#px) {
                my $left = $i >= 4 ? $px[$i - 4] : 0;
                $px[$i] = ($px[$i] + (($left + $up[$i]) >> 1)) & 255;
            }
        }
        elsif ($filter == 4) {
            for my $i (0 .. $#px) {
                my $a = $i >= 4 ? $px[$i - 4] : 0;
                my $b = $up[$i];
                my $c = $i >= 4 ? $up[$i - 4] : 0;
                my $p = $a + $b - $c;
                my ($pa, $pb, $pc) = (abs($p - $a), abs($p - $b), abs($p - $c));
                my $pred = ($pa <= $pb && $pa <= $pc) ? $a : ($pb <= $pc ? $b : $c);
                $px[$i] = ($px[$i] + $pred) & 255;
            }
        }
        $prev = pack 'C*', @px;
        push @rows, \@px;
    }

    return ($w, $h, \@rows);
}

sub write_png {
    my ($file, $w, $h, $rows) = @_;
    my $raw = '';
    for my $row (@$rows) {
        $raw .= "\0" . pack('C*', @$row);
    }
    my $compressed = compress($raw);

    my $ihdr = pack('NNCCCCC', $w, $h, 8, 6, 0, 0, 0);
    my $body = "\x89PNG\r\n\x1a\n" . chunk('IHDR', $ihdr) . chunk('IDAT', $compressed) . chunk('IEND', '');

    my ($dir) = $file =~ m{(.*)/[^/]+$};
    mkdir_for($dir) if $dir;
    open my $fh, '>:raw', $file or die "write $file: $!";
    print $fh $body;
    close $fh;
}

sub chunk {
    my ($type, $data) = @_;
    return pack('N', length $data) . $type . $data . pack('N', crc32($type . $data));
}

sub mkdir_for {
    my ($dir) = @_;
    my @parts = split m{/}, $dir;
    my $path = '';
    for my $part (@parts) {
        $path = $path eq '' ? $part : "$path/$part";
        mkdir $path unless -d $path;
    }
}

sub downscale2 {
    my ($w, $h, $rows) = @_;
    my @out;
    for my $y (0 .. ($h / 2) - 1) {
        my @row;
        for my $x (0 .. ($w / 2) - 1) {
            push @row, @{ $rows->[ $y * 2 ] }[ $x * 8 .. $x * 8 + 3 ];
        }
        push @out, \@row;
    }
    return ($w / 2, $h / 2, \@out);
}

sub copy_file {
    my ($from, $to) = @_;
    open my $in, '<:raw', $from or die "read $from: $!";
    local $/;
    my $bytes = <$in>;
    close $in;
    my ($dir) = $to =~ m{(.*)/[^/]+$};
    mkdir_for($dir) if $dir;
    open my $out, '>:raw', $to or die "write $to: $!";
    print $out $bytes;
    close $out;
}

my %layers;
for my $set (@sets) {
    my $texture = "$root/textures";

    for my $part ([ 'layer_1', 'humanoid', 32 ], [ 'layer_2', 'humanoid_leggings', 32 ]) {
        my ($suffix, $layer) = @$part;
        my ($w, $h, $rows) = read_png("$texture/models/armor/${set}_$suffix.png");
        my ($dw, $dh, $down) = downscale2($w, $h, $rows);
        write_png("$texture/entity/equipment/$layer/$set.png", $dw, $dh, $down);
    }

    my $horseSource = "$texture/entity/horse/armor/horse_armor_$set.png";
    copy_file($horseSource, "$texture/entity/equipment/horse_body/$set.png") if -f $horseSource;

    $layers{$set} = join ",\n",
        '    "humanoid": [{"texture": "tolkienmobs:' . $set . '"}]',
        '    "humanoid_leggings": [{"texture": "tolkienmobs:' . $set . '"}]',
        '    "horse_body": [{"texture": "tolkienmobs:' . $set . '"}]';

    my $json = "{\n  \"layers\": {\n$layers{$set}\n  }\n}\n";
    my $asset = "$root/equipment/$set.json";
    my ($dir) = $asset =~ m{(.*)/[^/]+$};
    mkdir_for($dir);
    open my $fh, '>', $asset or die "write $asset: $!";
    print $fh $json;
    close $fh;
}

print "equipment assets written for: @sets\n";
