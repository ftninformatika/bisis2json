package bisis.model.records.serializers;

import bisis.model.records.Record;


public interface RecordListener {
  public void handleRecord(Record rec);
}
