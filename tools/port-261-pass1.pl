#!/usr/bin/env perl
# Pass 1 of the 26.1 port: renames that carry no behaviour change.
# One process for the whole tree, in the shape of tools/port-1214-pass1.pl.
#
# Grounded in the decompiled 26.1.2 sources:
#   ResourceLocation -> Identifier                 (renamed; same package)
#   net.minecraft.Util -> net.minecraft.util.Util  (moved)
#
# Both are whole-word renames, so `Identifier.fromNamespaceAndPath`, `Identifier.parse`,
# `ResourceKey::location` and the like keep working. The only things that need care are the
# imports: the old `net.minecraft.resources.ResourceLocation` line becomes the new one, and a
# file that already imported `Identifier` must not end up with it twice.
#
# Idempotent: a second run finds the old names gone and changes nothing.
#
#   tools/port-261-pass1.pl [--dry-run]

use strict;
use warnings;
use File::Find;

my $root = 'src/main/java';
my $dry  = grep { $_ eq '--dry-run' } @ARGV;

my @files;
find( sub { push @files, $File::Find::name if /\.java$/ }, $root );

my $changed = 0;
my $renamed = 0;

for my $f (@files) {
    open my $in, '<:raw', $f or die "read $f: $!";
    local $/;
    my $c = <$in>;
    close $in;
    my $orig = $c;

    # ResourceLocation is Identifier now; the class kept its package.
    my $hits = () = $c =~ /\bResourceLocation\b/g;
    $renamed += $hits;
    $c =~ s/\bResourceLocation\b/Identifier/g;

    # Util moved down into net.minecraft.util.
    $c =~ s/^import net\.minecraft\.Util;(\r?)$/import net.minecraft.util.Util;$1/gm;
    $c =~ s/\bnet\.minecraft\.Util\./net.minecraft.util.Util./g;

    # A file that already imported Identifier now has the line twice.
    my %seen;
    $c =~ s{^(import [^\r\n]*;)\r?\n}{ $seen{$1}++ ? "" : "$1\n" }gme;

    # Anything that mentions Identifier needs its import, whichever name it used before.
    my $body = $c;
    $body =~ s/^import [^\n]*\n//gm;
    if ( $body =~ /\bIdentifier\b/ && $c !~ /^import net\.minecraft\.resources\.Identifier;/m ) {
        $c =~ s/^(package [^\n]*\n)/$1\nimport net.minecraft.resources.Identifier;\n/m;
    }

    if ( $c ne $orig ) {
        unless ($dry) {
            open my $out, '>:raw', $f or die "write $f: $!";
            print $out $c;
            close $out;
        }
        $changed++;
    }
}

print "pass 1 complete: $changed files changed, $renamed ResourceLocation references renamed\n";
