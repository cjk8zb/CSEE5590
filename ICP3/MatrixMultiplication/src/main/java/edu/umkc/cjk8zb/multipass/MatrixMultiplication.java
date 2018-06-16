package edu.umkc.cjk8zb.multipass;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;

public class MatrixMultiplication {

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        Path input = new Path(args[0]);
        Path middle = new Path(args[1]);
        Path output = new Path(args[2]);

        System.exit(runJobs(input, middle, output));
    }

    private static int runJobs(Path input, Path middle, Path output) throws InterruptedException, IOException, ClassNotFoundException {
        if (!runJob("FirstPass", input, middle, FirstPassMapper.class, FirstPassReducer.class)) {
            return 1;
        }
        if (!runJob("SecondPass", middle, output, SecondPassMapper.class, SecondPassReducer.class)) {
            return 2;
        }

        return 0;
    }

    private static boolean runJob(String name, Path input, Path output, Class<? extends Mapper> mapperClass, Class<? extends Reducer> reducerClass) throws IOException, ClassNotFoundException, InterruptedException {
        // Create configuration
        Configuration conf = new Configuration(true);

        // Create job
        Job job = new Job(conf, name);
        job.setJarByClass(mapperClass);

        // Setup MapReduce
        job.setMapperClass(mapperClass);
        job.setReducerClass(reducerClass);
        job.setNumReduceTasks(1);

        // Specify key / value
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // Input
        FileInputFormat.addInputPath(job, input);
        job.setInputFormatClass(TextInputFormat.class);

        // Output
        FileOutputFormat.setOutputPath(job, output);
        job.setOutputFormatClass(TextOutputFormat.class);

        // Delete output if exists
        FileSystem fileSystem = FileSystem.get(conf);
        if (fileSystem.exists(output)) {
            fileSystem.delete(output, true);
        }

        return job.waitForCompletion(true);
    }
}
