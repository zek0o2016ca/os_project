package LRU;

class Page {
    int id;
    int lastUsedTime; // Specific for LRU

    Page(int id, int lastUsedTime) {
        this.id = id;
        this.lastUsedTime = lastUsedTime;
    }
}

