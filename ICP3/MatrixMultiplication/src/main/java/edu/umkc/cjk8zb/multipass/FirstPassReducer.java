package edu.umkc.cjk8zb.multipass;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class FirstPassReducer extends
        Reducer<Text, IntWritable, Text, IntWritable> {

    private Text word = new Text();

    public void reduce(Text text, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException {
        int multi = 1;
        for (IntWritable value : values) {
            multi *= value.get();
        }

        // A row col B row col
        String[] parts = text.toString().split(" ");
        String rowA = parts[1];
        String colB = parts[5];
        word.set(String.join(" ", "R", rowA, colB));
        context.write(word, new IntWritable(multi));
    }

}
