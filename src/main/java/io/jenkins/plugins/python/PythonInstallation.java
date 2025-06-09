package io.jenkins.plugins.python;

import hudson.Extension;
import hudson.tools.ToolDescriptor;
import hudson.tools.ToolInstallation;
import hudson.tools.ToolProperty;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.List;

public class PythonInstallation extends ToolInstallation {
    @DataBoundConstructor
    public PythonInstallation(String name, String home, List<? extends ToolProperty<?>> properties) {
        super(name, home, properties);
    }

    @Extension
    @Symbol("python")
    public static class PythonToolDescriptor extends ToolDescriptor<PythonInstallation> {
        private PythonInstallation[] installations = new PythonInstallation[0];

        public PythonToolDescriptor() {
            load();
        }

        @Override
        public String getDisplayName() {
            return "Python";
        }

        @Override
        public PythonInstallation[] getInstallations() {
            return installations;
        }

        @Override
        public void setInstallations(PythonInstallation... installations) {
            this.installations = installations;
            save();
        }
    }

}
