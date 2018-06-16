package edu.umkc.cjk8zb.singlepass;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.*;

public class SinglePassReducer extends Reducer <Text, Text, Text, Text> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        Map<String, Integer> map = new HashMap<>();

        for (Text value: values) {
            String[] parts = value.toString().split(" ");
            String index = parts[0];
            int intValue = Integer.valueOf(parts[1]);
            Integer product = map.get(index);
            if (product == null) {
                product = intValue;
            } else {
                product *= intValue;
            }
            map.put(index, product);
        }

        int sumOfProducts = map.values().stream().reduce(0, Integer::sum);

        Text out = new Text();
        out.set(String.valueOf(sumOfProducts));
        context.write(key, out);
    }
}
