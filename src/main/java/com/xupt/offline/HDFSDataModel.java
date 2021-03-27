package com.xupt.offline;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;

import java.io.File;
import java.io.IOException;

/**
 * file文件内的数据格式：userID，itemID,preference value or null,timestamp....
 * That is,this is legal:
 *  </p>
 *
 *  <p>{@code 123,456,,129050099059}</p>
 *
 *  <p>But this isn't:</p>
 *
 *  <p>{@code 123,456,129050099059}</p>
 *
 *  <p>
 */
public class HDFSDataModel extends FileDataModel {
    public HDFSDataModel(File dataFile) throws IOException {
        super(dataFile);
    }
}
