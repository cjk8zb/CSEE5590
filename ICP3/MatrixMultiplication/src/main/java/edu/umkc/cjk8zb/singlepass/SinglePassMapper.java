package edu.umkc.cjk8zb.singlepass;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class SinglePassMapper extends Mapper<LongWritable, Text, Text, Text> {

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

        if (matrix.equals("A")) {
            for (int c = 1; c <= cols; c++) {
                write(matrix, rowPos, String.valueOf(c), colPos, cellValue, context);
            }
        } else {
            for (int r = 1; r <= rows; r++) {
                write(matrix, String.valueOf(r), colPos, rowPos, cellValue, context);
            }
        }

    }

    private void write(String matrix, String row, String col, String index, String value, Context context) throws IOException, InterruptedException {
        Text keyOut = new Text();
        Text valueOut = new Text();

        keyOut.set(String.join(" ", "R", row, col));
        valueOut.set(String.join(" ", index, value));

        context.write(keyOut, valueOut);
    }
}
