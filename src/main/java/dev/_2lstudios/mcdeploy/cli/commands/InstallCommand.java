package dev._2lstudios.mcdeploy.cli.commands;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.Callable;

import dev._2lstudios.mcdeploy.MCDeploy;
import dev._2lstudios.mcdeploy.cli.Logger;
import dev._2lstudios.mcdeploy.errors.MCDException;
import dev._2lstudios.mcdeploy.software.RunOptions;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "install", description = "Install a server artifact.")
public class InstallCommand implements Callable<Integer> {
    @Option(description = "Directory where install server", names = { "-d", "--directory" })
    private File cwd;

    @Option(description = "Java runtime binary", defaultValue = "java", names = { "-j", "--java" })
    private String java;

    @Parameters(index = "0", arity = "0..1", description = "Server software and version", defaultValue = "vanilla@latest")
    private String artifactId;

    @Override
    public Integer call() {
        MCDeploy mcd = new MCDeploy();
        RunOptions options = new RunOptions().setCWD(this.cwd).setJava(this.java);

        try {
            Logger.info("Fetching for versions...");
            mcd.prepare();

            Scanner input = new Scanner(System.in);
            System.out.println("What type of server do you want to install? Select one of the following list.");
            System.out.println(mcd.getSoftwareManager().softwares.keySet().toString());
            String serverType = input.nextLine();
            serverType = serverType.isEmpty() ? "vanilla" : serverType;

            System.out.println("What version do you want to install?");
            System.out.println(mcd.getSoftwareManager().softwares.get(serverType).versions);
            String serverVersion = input.nextLine();
            serverVersion = serverVersion.isEmpty() ? "latest" : serverVersion;

            artifactId = serverType + "@" + serverVersion;
            input.close();
            
            Logger.info("Preparing for download artifact: " + artifactId);
            mcd.install(artifactId, options);
            Logger.info("Installed successfully.");

        } catch (MCDException e) {
            Logger.crit(e.getMessage());
        }

        return 0;
    }
}
