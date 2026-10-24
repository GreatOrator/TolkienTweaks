#!/usr/bin/perl
#
# The second half of the save-data move: the calls a ValueInput / ValueOutput do not
# answer on their own.
#
#   output.put("k", handler.serializeNBT(regs));        -> handler.serialize(output.child("k"));
#   handler.deserializeNBT(regs, input.getCompound("k")) -> handler.deserialize(input.childOrEmpty("k"));
#   super.saveAdditional(tag, regs)                     -> super.saveAdditional(output)
#   super.loadAdditional(tag, regs)                     -> super.loadAdditional(input)
#
# ItemStackHandler, FluidTank and the other ValueIOSerializable helpers write themselves
# into a ValueOutput now, so the nested compound is a child of the block's own output
# rather than a tag stuffed into it - the keys inside it are unchanged.
#
# A few sites still need a hand (loose lists of items, the spawner's mob list, the
# chest-code string); javac lists those afterwards.
#
#   tools/port-261-value-fixes.pl [--dry-run]

use strict;
use warnings;
use File::Find;

my $dry = grep { $_ eq '--dry-run' } @ARGV;

my @files;
find( sub { push @files, $File::Find::name if /\.java$/ }, 'src/main/java' );

my $changed = 0;

for my $f (@files) {
    open my $in, '<:raw', $f or die "read $f: $!";
    local $/;
    my $c = <$in>;
    close $in;
    my $orig = $c;

    $c =~ s/output\.put\("([^"]+)",\s*([\w.]+(?:\(\))?)\.serializeNBT\(\w+\)\);/$2.serialize(output.child("$1"));/g;
    $c =~ s/([\w.]+(?:\(\))?)\.deserializeNBT\(\w+,\s*input\.getCompound\("([^"]+)"\)\)/$1.deserialize(input.childOrEmpty("$2"))/g;

    $c =~ s/super\.saveAdditional\((\w+),\s*\w+\)/super.saveAdditional($1)/g;
    $c =~ s/super\.loadAdditional\((\w+),\s*\w+\)/super.loadAdditional($1)/g;

    next if $c eq $orig;
    unless ($dry) {
        open my $out, '>:raw', $f or die "write $f: $!";
        print $out $c;
        close $out;
    }
    $changed++;
}

printf "value fixes: %d files changed%s\n", $changed, $dry ? ' (dry run)' : '';
