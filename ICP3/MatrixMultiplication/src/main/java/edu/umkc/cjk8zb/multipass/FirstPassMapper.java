package edu.umkc.cjk8zb.multipass;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class FirstPassMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    private Text word = new Text();
    private int rows = -1;
    private int cols = -1;

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String stringValue = value.toString();

        // Skip empty lines and comments
        if (stringValue.length() == 0 || stringValue.charAt(0) == '#') {
            return;
        }

        String[] strings = stringValue.split("\t");
        if (rows < 0) {
            rows = Integer.parseInt(strings[0]);
            cols = Integer.parseInt(strings[1]);
            return;
        }

        String[] parts = strings[0].split(" ");
        String cellValue = strings[1];

        String matrix = parts[0];
        String rowPos = parts[1];
        String colPos = parts[2];
        int intValue = Integer.parseInt(cellValue);

        if (matrix.equals("A")) {
            for (int c = 1; c <= cols; c++) {
                write(matrix, rowPos, String.valueOf(c), colPos, cellValue, context);
                word.set(String.join(" ", "A", rowPos, colPos, "B", colPos, String.valueOf(c)));
                context.write(word, new IntWritable(intValue));
            }
        } else {
            for (int r = 1; r <= rows; r++) {
                word.set(String.join(" ", "A", String.valueOf(r), rowPos, "B", rowPos, colPos));
                context.write(word, new IntWritable(intValue));
            }
        }
    }

    private void write(String matrix, String row, String col, String index, String value, Context context) throws IOException, InterruptedException {

//        word.set(String.join(" ", "A", rowPos, colPos, "B", colPos, String.valueOf(c)));
//        word.set(String.join(" ", "A", String.valueOf(r), rowPos, "B", rowPos, colPos));
//
//        word.set(String.join(" ", "A", row, col, "B", col, index));
//        word.set(String.join(" ", "A", index, row, "B", row, col));

        Text keyOut = new Text();
        Text valueOut = new Text();

        keyOut.set(String.join(" ", "R", row, col));
        valueOut.set(String.join(" ", index, value));

        context.write(keyOut, valueOut);
    }
}
