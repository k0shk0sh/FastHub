package in.uncod.android.bypass;

class Document {

    Element[] elements;

    public Document(Element[] elements) {
        this.elements = elements;
    }

    int getElementCount() {
        return elements.length;
    }

    Element getElement(int pos) {
        return elements[pos];
    }
}
