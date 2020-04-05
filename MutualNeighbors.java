
//package edu.ndsu.cs.roadley.pages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;

// Create a maven project, use anything for artifact id
// In pom.xml add hadoop dependency (libraries)
// Need hadoop installed to compile project
// Open in eclipse
// Do import maven project, not open
// Use winscp to drag and drop files to server, put in your home folder
// Move java file to server
// Use powershell, ssh roadley@urlserver, use pwd to tell you where you are
// Or putty, command line into the server folder where your java file is
// Type: java -version to see if java is already set up
// else: whereis java
// run line: hadoop com.sun.tools.javac.Main WordCount.java
// run jar cf line to put everything in jar file
// will call jar file to start project

public class MutualNeighbors extends Configured implements Tool {
	public static class Map extends Mapper<Object, Text, Text, Text> { // can use text or int writable for the last one
																		// - this is the output

		private Text peerText = new Text();
		private Text valueText = new Text();

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();
			String[] lineArray = line.split("\\s+"); // splits completely, so 0 is peer, 1 would be first neighbor, 2 is
														// second neighbor
			String peer = lineArray[0].trim();
			String[] neighbors = new String[lineArray.length - 1];
			for (int i = 0; i < neighbors.length; i++) {
				neighbors[i] = lineArray[i + 1];
			}

			ArrayList<String> keys = new ArrayList<String>();

			for (int i = 0; i < neighbors.length; i++) {
				for (int j = i + 1; j < neighbors.length; j++) {
					String newKey = neighbors[i] + "," + neighbors[j];
					keys.add(newKey);
				}
			}

			for (String temp : keys) {
				peerText.set(peer);
				valueText.set(temp);
				context.write(valueText, peerText);
			}
		}

	}

	public static class Reduce extends Reducer<Text, Text, Text, Text> {

		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

			
			Text result = new Text();
			StringBuilder sb = new StringBuilder();

			int count = 0;
			for (Text peer : values) {
				// if it's the first time through
				if (count == 0) {
					sb.append("{");
				}

				sb.append(peer);
				sb.append(",");
				count++;
			}

			int length = sb.length();

			// Remove the last comma
			sb.deleteCharAt(length - 1);

			// Add a closing curly brace and colon
			sb.append("} :");

			// Convert StringBuilder to Text
			result.set(new Text(sb.toString()));

			// Only write to output if there is shared neighbor
			if(count > 1) {
				context.write(result, key);
			}
			

		}

	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

		Configuration conf = new Configuration();

		// Create a job with name MutualNeighbors
		Job job = Job.getInstance(conf, "MutualNeighbors");
		job.setJarByClass(MutualNeighbors.class);
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		// Set output key type
		job.setMapOutputKeyClass(Text.class);
		job.setOutputKeyClass(Text.class);

		// Set output value type
		job.setMapOutputValueClass(Text.class);
		job.setOutputValueClass(Text.class);

		// Set the HDFS path of the input
		FileInputFormat.addInputPath(job, new Path(args[0]));

		// Set the HDFS path for the output
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);

	}

	@Override
	public int run(String[] args) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

}