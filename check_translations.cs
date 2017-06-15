using System;
using System.Collections.ObjectModel;
using System.IO;
using System.Linq;
using System.Management.Automation;
using System.Xml;

void Main(string[] args)
{
	string RootDir = System.Reflection.Assembly.GetExecutingAssembly().Location;
	string ResDir = Path.Combine(RootDir, @"app\src\main\res");

	PowerShell ps = PowerShell.Create();

	int TotalFiles = 0,
		TotalIssues = 0;

	foreach (string dir in Directory.GetDirectories(ResDir, "values-*").Where(d => File.Exists(Path.Combine(d, @"strings.xml")))) {
		Console.WriteLine(@"Checking ""{0}""...", dir);

		XmlDocument xmlFile = new XmlDocument();
		xmlFile.Load(Path.Combine(dir, @"strings.xml"));
		XmlElement xRoot = xmlFile.DocumentElement;

		bool wasAdded = false;

		foreach (XmlNode xmlNode in xRoot) {
			if (xmlNode.Attributes == null) {
				continue;
			}
			if (xmlNode.Attributes.Count > 0) {
				foreach (XmlNode attr in xmlNode.Attributes) {
					if (attr == null) {
						continue;
					}
					if (attr.Name == "translatable") {
						TotalIssues++;
						ps.AddCommand("Add-AppveyorMessage");
						ps.AddArgument(String.Format(@"{0}=""{1}"" in {2}", attr.Name, attr.Value, xmlNode.OuterXml));
						Console.WriteLine(@" {0}=""{1}"" in {2}", attr.Name, attr.Value, xmlNode.OuterXml);
						if (wasAdded) {
							continue;
						}
						TotalFiles++;
						wasAdded = true;
					}
				}
			}
		}
	}

	Console.WriteLine("Found {0} issue(s) in {1} file(s).", TotalIssues, TotalFiles);
	if (TotalIssues != 0) {
		ps.Invoke();
		Environment.Exit(101);
	}
}