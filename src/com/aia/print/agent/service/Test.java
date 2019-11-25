package com.aia.print.agent.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Test {

	public static void main(String[] args) throws ParseException {
		String fileName = "Co3_Reconcilation Report_20191121_0242.csv";
		String targetFileName="Co3_Reconcilation_Report_20191121";
		if(fileName.contains(targetFileName)) {
			System.out.println(fileName);
			System.out.println(targetFileName);
		}else {
			System.out.println("MismatchSystem.out.println(targetFileName);");
		}
		
		
		String inputDate = "14/11/2019";
		SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
		System.out.println(inputFormat.parseObject(inputDate));
		System.out.println(outputFormat.format(inputFormat.parseObject(inputDate)));
		
	}

}
