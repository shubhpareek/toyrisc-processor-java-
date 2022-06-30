package processor.memorysystem;
public class CacheLine {
    int data;
    int tag;

    public CacheLine() {
        this.tag = -1;
        this.data = -1;
    }

    public void setData(int data){
        this.data = data;
    }

    public int getData() {
        return this.data;
    }

    public void setTag(int tag){
        this.tag = tag;
    }

    public int getTag() {
        return this.tag;
    }
}