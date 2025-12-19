package Clock;

class PageFrame {
    int pageId;
    int referenceBit;

    public PageFrame() {
        this.pageId = -1; // -1 = Empty Frame
        this.referenceBit = 0;  // 0 = Not Referenced, 1 = Referenced
    }
}
