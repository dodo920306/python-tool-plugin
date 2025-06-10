package io.jenkins.plugins.python;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.tools.ToolInstallation;
import hudson.tools.ToolInstaller;
import hudson.tools.ToolInstallerDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PythonInstaller extends ToolInstaller {
    private final String version;

    @DataBoundConstructor
    public PythonInstaller(String label, String version) {
        super(label);
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public FilePath performInstallation(ToolInstallation tool, Node node, TaskListener log) throws IOException, InterruptedException {
        FilePath expectedLocation = preferredLocation(tool, node);
        log.getLogger().println("Installing Python version: " + version);

        String url = "https://www.python.org/ftp/python/" + version + "/Python-" + version + ".tgz";
        FilePath archive = expectedLocation.child("python-" + version + ".tgz");

        if (!archive.exists()) {
            log.getLogger().println("Downloading from: " + url);
            archive.copyFrom(new URL(url));
        }

        log.getLogger().println("Extracting Python...");
        expectedLocation.mkdirs();
        archive.untar(expectedLocation, FilePath.TarCompression.GZIP);

        return expectedLocation;
    }

    @Extension
    public static class DescriptorImpl extends ToolInstallerDescriptor<PythonInstaller> {
        @NonNull
        public String getDisplayName() {
            return "Install Python";
        }

        public List<String> getAvailableVersions() {
            try {
                URL url = new URL("https://www.python.org/ftp/python/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    List<SemVer> versions = new ArrayList<>();
                    String line;
                    Pattern pattern = Pattern.compile("<a href=\"([0-9]+)\\.([0-9]+)\\.([0-9]+)/\">");

                    while ((line = reader.readLine()) != null) {
                        Matcher matcher = pattern.matcher(line);
                        if (matcher.find()) {
                            int major = Integer.parseInt(matcher.group(1));
                            int minor = Integer.parseInt(matcher.group(2));
                            int patch = Integer.parseInt(matcher.group(3));

                            if (major > 3 || (major == 3 && minor >= 6)) {
                                versions.add(new SemVer(major, minor, patch));
                            }
                        }
                    }

                    versions.sort((v1, v2) -> {
                        if (v1.major() != v2.major()) {
                            return Integer.compare(v2.major(), v1.major());
                        } else if (v1.minor() != v2.minor()) {
                            return Integer.compare(v2.minor(), v1.minor());
                        } else {
                            return Integer.compare(v2.patch(), v1.patch());
                        }
                    });

                    return versions.stream().map(SemVer::toString).toList();
                }
            } catch (IOException e) {
                return Arrays.asList("3.12.11", "3.11.13", "3.10.18", "3.9.23", "3.8.20");
            }
        }

        @Override
        public boolean isApplicable(Class<? extends ToolInstallation> toolType) {
            return PythonInstallation.class.isAssignableFrom(toolType);
        }
    }

}
