package com.mymdl.george.myymdll;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FtpUploader extends AsyncTask<String,Void,String> {

    public String file_name;
    private Context myContext;
    public FTPClient mFTPClient = null;

    public FtpUploader(Context context)
    {
        this.myContext = context;
    }

    protected void onPreExecute()
    {

    }

    private static void showServerReply(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                System.out.println("SERVER: " + aReply);
            }
        }
    }


    @Override
    protected String doInBackground(String... arg0) {
        String filesToUpload = arg0[0];
        file_name = arg0[1];

        String server = "ftp.cluster006.hosting.ovh.net";
        int port = 21;
        String user = "federatita-appli";
        String pass = "Guillaume9275";
        FTPClient ftpClient = new FTPClient();

        try {
            ftpClient.connect(server, port);
            showServerReply(ftpClient);
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                System.out.println("Operation failed. Server reply code: " + replyCode);

            }
            boolean success = ftpClient.login(user, pass);
            showServerReply(ftpClient);
            if (!success) {
                System.out.println("Could not login to the server");

            } else {
                System.out.println("LOGGED IN SERVER");

                // lists files and directories in the current working directory
                /*FTPFile[] files = ftpClient.listFiles();

                // iterates over the files and prints details for each
                DateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                for (FTPFile file : files) {
                    String details = file.getName();
                    if (file.isDirectory()) {
                        details = "[" + details + "]";
                    }
                    details += "\t\t" + file.getSize();
                    details += "\t\t" + dateFormater.format(file.getTimestamp().getTime());
                    System.out.println(details);
                }*/

                // Changes working directory
                success = ftpClient.changeWorkingDirectory("/COVID19/imagesEvent");
                showServerReply(ftpClient);

                Toast.makeText(this.myContext,"FTP : OK !", Toast.LENGTH_LONG).show();

                /*if (success) {
                    System.out.println("Successfully changed working directory.");
                } else {
                    System.out.println("Failed to change working directory. See server's reply.");
                }



                // APPROACH #1: uploads first file using an InputStream
                File firstLocalFile = new File("C:/Users/theom/Desktop/FTP_TEST/src/com/company/test.jpg");

                String firstRemoteFile = "test.jpg";
                InputStream inputStream = new FileInputStream(firstLocalFile);

                System.out.println("Start uploading first file");
                boolean done = ftpClient.storeFile(firstRemoteFile, inputStream);
                inputStream.close();
                if (done) {
                    System.out.println("The first file is uploaded successfully.");
                }*/

            }
        } catch (IOException ex) {
            System.out.println("Oops! Something wrong happened");
            ex.printStackTrace();
        }


        return "done";
    }
}
