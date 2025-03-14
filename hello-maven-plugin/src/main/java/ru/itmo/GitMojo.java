package ru.itmo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.IOException;

@Mojo(name = "git", defaultPhase = LifecyclePhase.PACKAGE)
public class GitMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try(Repository existingRepo = new FileRepositoryBuilder()
                .setGitDir(project.getBasedir())
                .build()
        ) {

            getLog().info("Current branch is: " + existingRepo.getBranch());

        } catch (IOException e) {
            getLog().info("ERROR: " + e.getMessage());
        }
    }
}
