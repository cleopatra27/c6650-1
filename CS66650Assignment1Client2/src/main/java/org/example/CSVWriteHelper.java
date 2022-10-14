package org.example;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.util.List;
import java.util.Objects;

public class CSVWriteHelper {
    private final String pathName;

    public CSVWriteHelper(String pathName) {
        this.pathName = pathName;
    }

    public String csvWriterAll(List<String[]> stringArray, String[] headerRecord) throws Exception {
        CSVWriter writer = new CSVWriter(new FileWriter(this.pathName));
        writer.writeNext(headerRecord);
        writer.writeAll(stringArray);
        writer.close();
        return "done";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CSVWriteHelper)) {
            return false;
        }
        CSVWriteHelper that = (CSVWriteHelper) o;
        return Objects.equals(pathName, that.pathName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pathName);
    }

    @Override
    public String toString() {
        return "CSVWriteHelper{" + "pathName='" + pathName + '\'' + '}';
    }
}