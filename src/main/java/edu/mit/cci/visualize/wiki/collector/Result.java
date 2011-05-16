package edu.mit.cci.visualize.wiki.collector;

public class Result {

    private String data;
    private int count;
    private String nextId;
    private boolean archive;

    public Result() {
        data = new String();
        count = 0;
        nextId = "0";
        archive = false;
    }

    public void setResult(final String _data) {
        data = _data;
    }

    public void append(final String _data) {
        data += _data;
        count++;
    }

    public String getResult() {
        return data;
    }

    public int getCount() {
        return count;
    }

    public void clear() {
        data = "";
        count = 0;
        nextId = "0";
        archive = false;
    }

    public void setNextId(final String _nextId) {
        nextId = _nextId;
    }

    public String getNextId() {
        return nextId;
    }

    public boolean hasNextId() {
        return !"0".equals(nextId);
    }

    public boolean getArchive() {
        return archive;
    }

    public void setArchive(final boolean flag) {
        archive = flag;
    }
}
