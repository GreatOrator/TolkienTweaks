#!/usr/bin/perl
#
# 26.1 replaced the CompoundTag that entities and block entities saved themselves into
# with ValueOutput / ValueInput, and dropped the HolderLookup.Provider argument:
#
#   addAdditionalSaveData(CompoundTag tag)                       -> addAdditionalSaveData(ValueOutput output)
#   readAdditionalSaveData(CompoundTag tag)                      -> readAdditionalSaveData(ValueInput input)
#   saveAdditional(CompoundTag tag, HolderLookup.Provider regs)  -> saveAdditional(ValueOutput output)
#   loadAdditional(CompoundTag tag, HolderLookup.Provider regs)  -> loadAdditional(ValueInput input)
#
# The parameter is renamed through the whole body, and the scalar readers become the
# Or-with-default calls 26.1 uses, because ValueInput#getInt and friends return Optionals:
#
#   input.getInt("Variant")   -> input.getIntOr("Variant", 0)
#   input.getBoolean("Flag")  -> input.getBooleanOr("Flag", false)
#
# Writes (`putInt`, `putString`, `putBoolean`, ...) already match ValueOutput and are left
# alone. Nested compounds, lists, `contains`, bare `get` and anything holding a Provider
# are *not* rewritten - those need a codec or a child object and are done by hand, with
# javac pointing at them.
#
# It works line by line rather than by string offsets: a method body is small, and
# rewriting whole lines keeps a bad match from shifting the rest of the file.
#
#   tools/port-261-save-data.pl [--dry-run]

use strict;
use warnings;
use File::Find;

my $root = 'src/main/java';
my $dry  = grep { $_ eq '--dry-run' } @ARGV;

my @files;
find( sub { push @files, $File::Find::name if /\.java$/ }, $root );

my %default = (
    getBoolean => 'false',
    getByte    => '(byte) 0',
    getShort   => '(short) 0',
    getInt     => '0',
    getLong    => '0L',
    getFloat   => '0.0F',
    getDouble  => '0.0',
    getString  => '""',
);

my ( $methods, $files_changed ) = ( 0, 0 );

for my $f (@files) {
    open my $in, '<:raw', $f or die "read $f: $!";
    my @lines = <$in>;
    close $in;
    my $orig = join '', @lines;

    for ( my $i = 0; $i <= $#lines; $i++ ) {
        next unless $lines[$i] =~ /^(\s*)(?:(?:public|protected|private|default)\s+)?(?:static\s+)?void\s+(addAdditionalSaveData|readAdditionalSaveData|saveAdditional|loadAdditional|save[A-Z]\w*|load[A-Z]\w*)\s*\(\s*CompoundTag\s+(\w+)\s*(?:,\s*HolderLookup\.Provider\s+\w+\s*)?\)\s*\{/;

        my ( $name, $param ) = ( $2, $3 );

        # Find the line that closes this body. Bodies here never carry a brace in a
        # string, so counting the braces on each line is enough.
        my $depth = 0;
        my $end;
        for ( my $j = $i; $j <= $#lines; $j++ ) {
            $depth += ( $lines[$j] =~ tr/{// );
            $depth -= ( $lines[$j] =~ tr/}// );
            if ( $depth == 0 ) { $end = $j; last; }
        }
        next unless defined $end;

        my $is_out = $name =~ /^(?:add|save)/;
        my $new    = $is_out ? 'output' : 'input';
        my $type   = $is_out ? 'ValueOutput' : 'ValueInput';

        for my $k ( $i .. $end ) {
            $lines[$k] =~ s/\b\Q$param\E\b/$new/g;
            next if $is_out;
            $lines[$k] =~ s/\.(getBoolean|getByte|getShort|getInt|getLong|getFloat|getDouble|getString)\s*\(\s*("(?:[^"\\]|\\.)*")\s*\)/'.' . $1 . 'Or(' . $2 . ', ' . $default{$1} . ')'/ge;
        }

        $lines[$i] =~ s/\(\s*CompoundTag\s+\Q$new\E\s*(?:,\s*HolderLookup\.Provider\s+\w+\s*)?\)/\($type $new\)/;

        $i = $end;
        $methods++;
    }

    my $now = join '', @lines;
    if ( $now ne $orig ) {
        unless ($dry) {
            open my $out, '>:raw', $f or die "write $f: $!";
            print $out $now;
            close $out;
        }
        $files_changed++;
    }
}

printf "save data: %d methods rewritten, %d files changed%s\n",
    $methods, $files_changed, $dry ? ' (dry run)' : '';
