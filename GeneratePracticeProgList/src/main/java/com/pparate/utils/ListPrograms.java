package com.pparate.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ListPrograms {
    static Logger logger = LogManager.getLogger();
    static String pathName = "D:\\Projects\\DSAlgo";
    static File rootDir = new File(pathName);
    static int threads = 8;
    static ExecutorService threadExecutor = Executors.newFixedThreadPool(threads);
    static ArrayList<Program> programList = new ArrayList<>();
    static Set<String> displayAttrib = new HashSet<>();

    public static void main(String[] args) {
        logger.debug("Initiated processing");
        /*
            Logic -
                Stage 1: Get list of files and folders in curr dir
                Stage 2: Submit processing of file through thread
                         Recursively call method to process each folder
                Stage 3: Generate Program object for each file in
                         thread execution and append it to list.
                Stage 4: On completion, generate HTML from list.
         */
        init();
        process(rootDir);
        threadExecutor.shutdown();
        try {
            threadExecutor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            logger.error("Thread executor await interrupted");
            e.printStackTrace();
        }

        generateHTML();

    }

    public static void init() {
        // List of attributes to display in html.
        displayAttrib.add("description");
        displayAttrib.add("problem");
        displayAttrib.add("author");
    }

    public static void process(File currDir) {

        File[] rootList = currDir.listFiles();
        for (final File currFile : rootList) {

            if (currFile.isDirectory()) {
                process(currFile);
            } else {
                // Its file. Submit thread for each file processing.
                threadExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        processFile(currFile);
                    }
                });
            }
        }
    }

    static void processFile(File file) {
        //logger.debug("Processing file: " + file.getAbsolutePath());

        // Read file and get first comment section.
        // Send it for further processing
        ArrayList<String> commentText = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = "";
            Boolean firstCommment = false;
            CommentType commentType = null;

            // Get first comment section from file
            while ((line = br.readLine()) != null) {
                // is first comment, if yes mark
                if ((!firstCommment) && (line.trim().startsWith("/*") || line.trim().startsWith("//"))) {
                    firstCommment = true;
                    commentType = (line.trim().startsWith("/*") ? CommentType.MULTI_LINE : CommentType.SINGLE_LINE);
                }

                //
                if (firstCommment) {
                    commentText.add(line);

                    // check if first comment ends here
                    if (((commentType == CommentType.MULTI_LINE) && line.contains("*/")) ||
                            ((commentType == CommentType.SINGLE_LINE) && line.trim() != null && line.trim() != "" &&
                                    !line.contains("//"))) {
                        firstCommment = false;
                        break;
                    }
                    // If we are here, it means we are in first comment section
                    // so process.
                }
            }

            // got the comment section. Now extract key value tags from text.
            if (!commentText.isEmpty()) {
                HashMap<String, String> tags = new HashMap<>();
                tags = processTags(commentText);

                Iterator itr = tags.keySet().iterator();
                Program program = new Program(file.getName(), file.getAbsolutePath());
                while (itr.hasNext()) {
                    String key = (String) itr.next();
                    program.addAttrib(key, tags.get(key));
                    logger.info(file.getName() + " - " + key + " : " + tags.get(key));
                }
                programList.add(program);
            }
        } catch (FileNotFoundException e) {
            logger.error("File " + file.getAbsolutePath() + " not found.");
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("File IO error while reading file " + file.getAbsolutePath());
            e.printStackTrace();
        }
    }


    static HashMap<String, String> processTags(ArrayList<String> text) {

        String key = null;
        String value = null;
        HashMap<String, String> tagMap = new HashMap<>();

        for (String line : text) {

            // if line does not contain # keep appending to value
            // if key is not null. Key comes first.
            if (!line.contains("#") && key != null) {
                String newLine = removeComments(line);
                if (newLine != null && newLine != "")
                    value = (value + " " + newLine).trim();
            } else {
                if (line.contains("#") && line.contains((":"))) {
                    if (key != null && value != null && value != "" && value != "\n") {
                        tagMap.put(key, value);
                    }
                    key = line.substring(line.indexOf('#') + 1, line.indexOf(':')).trim();
                    value = removeComments(line.substring(line.indexOf(':') + 1));
                }
            }
        }
        // last iteration will not insert values, so handle after loop
        if (key != null) {
            tagMap.put(key, value);
        }
        return tagMap;
    }

    static String removeComments(String text) {
        String returnText = text.replace("//", "")
                .replace("*/", "")
                .replace("/*", "").trim();

        // remove starting * in block level comment lines.
        return returnText.trim().startsWith("*") ? returnText.trim().substring(1) : returnText;
    }

    static void generateHTML() {
        String htmlFileName = "programList.html";
        File htmlFile = new File(pathName + "\\" + htmlFileName);

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(htmlFile));

            programList.sort(new Comparator<Program>() {
                @Override
                public int compare(Program o1, Program o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });

            for (Program program : programList) {
                HashMap<String, String> attribs = program.getAttribs();

                Set<String> keys = attribs.keySet();

                // sort attributes by key TBI
                Iterator itr = keys.iterator();

                String writeString = "<div style='margin: 15px 0;'>";
                Boolean printProgram = false;
                while (itr.hasNext()) {
                    printProgram = true;
                    String key = (String) itr.next();
                    writeString += attribs.get(key) +
                            "<br/>";
                }
                if (printProgram) {
                    writeString += "<a href='" + program.getPath() + "'>" +
                            program.getName() + "</a>";
                }
                writeString += "</div>";
                bw.write(writeString);
                bw.flush();
                logger.debug(writeString);
            }

            bw.close();

        } catch (IOException e) {
            logger.error("Error writing html file.");
            e.printStackTrace();
        }
    }
}


enum CommentType {
    SINGLE_LINE, MULTI_LINE
}