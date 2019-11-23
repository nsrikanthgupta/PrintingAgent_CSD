package com.aia.print.agent.service;

public class Test {

	public static void main(String[] args) {
		String fileName = "Co3_Reconcilation Report_20191121_0242.csv";
		String targetFileName="Co3_Reconcilation_Report_20191121";
		if(fileName.contains(targetFileName)) {
			System.out.println(fileName);
			System.out.println(targetFileName);
		}else {
			System.out.println("MismatchSystem.out.println(targetFileName);");
		}
	}

}
