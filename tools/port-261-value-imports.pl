#!/usr/bin/perl
#
# Add the ValueInput / ValueOutput imports to every file that now names them.
# They go in sorted position among the file's existing imports so the block stays
# in the order the rest of the tree uses.
#
#   tools/port-261-value-imports.pl [--dry-run]
use strict;
use warnings;
use File::Find;

my $dry = grep { $_ eq '--dry-run' } @ARGV;

my @files;
find( sub { push @files, $File::Find::name if /\.java$/ }, 'src/main/java' );

my $changed = 0;

for my $f (@files) {
    open my $in, '<:raw', $f or die "read $f: $!";
    my @lines = <$in>;
    close $in;

    my $body = join '', @lines;
    $body =~ s/^import [^\n]*\n//gm;

    my @needed = grep { $body =~ /\b$_\b/ } qw(ValueInput ValueOutput);
    next unless @needed;

    my $orig = join '', @lines;
    for my $sym (@needed) {
        my $fqcn = "import net.minecraft.world.level.storage.$sym;";
        next if grep { /^\s*\Q$fqcn\E/ } @lines;

        my $at = -1;
        for my $i ( 0 .. $#lines ) {
            next unless $lines[$i] =~ /^import /;
            my $text = $lines[$i];
            $text =~ s/\s+$//;
            if ( $text gt $fqcn ) { $at = $i; last; }
            $at = $i + 1;
        }
        die "no imports in $f" if $at < 0;
        splice @lines, $at, 0, "$fqcn\n";
    }

    my $now = join '', @lines;
    next if $now eq $orig;

    unless ($dry) {
        open my $out, '>:raw', $f or die "write $f: $!";
        print $out $now;
        close $out;
    }
    $changed++;
}

printf "value imports: %d files changed%s\n", $changed, $dry ? ' (dry run)' : '';
