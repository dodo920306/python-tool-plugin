package io.jenkins.plugins.python;

import edu.umd.cs.findbugs.annotations.NonNull;

public record SemVer(int major, int minor, int patch) {
    @NonNull
    public String toString() {
        return String.format("%d.%d.%d", major, minor, patch);
    }
}
