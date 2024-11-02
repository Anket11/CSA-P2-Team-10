package Interface;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

@SuppressWarnings("serial")
public class LimitedDocument extends PlainDocument {

    private int _maxLength = -1;
    private String _allowCharAsString = null;

    public LimitedDocument(int maxLength) {
        super();
        this._maxLength = maxLength;
    }

    // Set the allowed characters
    public void setAllowChar(String str) {
        _allowCharAsString = str;
    }

    @Override
    public void insertString(int offset, String str, AttributeSet attrSet) throws BadLocationException {
        if (str == null) return;

        // Check if input matches allowed characters, if defined
        if (_allowCharAsString != null && str.length() == 1) {
            if (_allowCharAsString.indexOf(str) == -1) return;
        }

        // Verify max length restriction
        String strOldValue = getText(0, getLength());
        if (_maxLength != -1 && (strOldValue.length() + str.length() > _maxLength)) return;

        super.insertString(offset, str, attrSet);
    }
}
