package io.jenkins.plugins.python;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.EnvVars;
import hudson.Extension;
import hudson.Util;
import hudson.model.EnvironmentSpecific;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.slaves.NodeSpecific;
import hudson.tools.ToolDescriptor;
import hudson.tools.ToolInstallation;
import hudson.tools.ToolInstaller;
import hudson.tools.ToolProperty;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.List;

public class PythonInstallation extends ToolInstallation implements EnvironmentSpecific<PythonInstallation>, NodeSpecific<PythonInstallation> {
    @DataBoundConstructor
    public PythonInstallation(String name, String home, List<? extends ToolProperty<?>> properties) {
        super(name, home, properties);
    }

    @Override
    public PythonInstallation forEnvironment(EnvVars environment) {
        return new PythonInstallation(getName(), environment.expand(getHome()), getProperties());
    }

    @Override
    public PythonInstallation forNode(@NonNull Node node, TaskListener log) throws IOException, InterruptedException {
        return new PythonInstallation(getName(), translateFor(node, log), getProperties().toList());
    }

    @Override
    public void buildEnvVars(EnvVars env) {
        String home = Util.fixEmpty(getHome());
        if (home != null) {
            env.put("PATH+PYTHON", home + "/bin");
        }
    }

    @Extension
    @Symbol("python")
    public static class DescriptorImpl extends ToolDescriptor<PythonInstallation> {
        @Override
        public String getDisplayName() {
            return "Python";
        }

        @Override
        public PythonInstallation[] getInstallations() {
            load();
            return super.getInstallations();
        }

        @Override
        public void setInstallations(PythonInstallation... installations) {
            super.setInstallations(installations);
            save();
        }

        @Override
        public List<? extends ToolInstaller> getDefaultInstallers() {
            return super.getDefaultInstallers();
        }
    }

}
