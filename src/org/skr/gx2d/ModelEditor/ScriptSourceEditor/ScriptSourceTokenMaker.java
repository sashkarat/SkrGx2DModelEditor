package org.skr.gx2d.ModelEditor.ScriptSourceEditor;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMaker;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;
import org.skr.SkrScript.Builder;

import javax.swing.text.Segment;
import java.util.StringTokenizer;

/**
 * Created by rat on 21.11.14.
 */
public class ScriptSourceTokenMaker extends AbstractTokenMaker {
    @Override
    public TokenMap getWordsToHighlight() {
        TokenMap tm = new TokenMap();
        for ( String keyStr : Builder.getKeywords() ) {
            int tokenType = Token.RESERVED_WORD;
            tm.put(keyStr, tokenType );
        }
        return tm;
    }

    @Override
    public void addToken(Segment segment, int start, int end, int tokenType, int startOffset) {
        // This assumes all keywords, etc. were parsed as "identifiers."

        if ( end >= segment.array.length ) {
            System.err.println("ScriptSourceTokenMaker.addToken(...) Wrong end: " + end +
                    " limit: " + segment.array.length );
            return;
        }

        if (tokenType==Token.IDENTIFIER) {
            int value = wordsToHighlight.get(segment, start, end);
            if (value != -1) {
                tokenType = value;
            }
        }
        super.addToken(segment, start, end, tokenType, startOffset);
    }

    @Override
    public Token getTokenList(Segment text, int initialTokenType, int startOffset) {
        resetTokenList();

        char[] array = text.array;
        int offset = text.offset;
        int count = text.count;

        int newStartOffset = startOffset - offset;


        if ( count == 0 || initialTokenType != Token.NULL) {
            addNullToken();
            return firstToken;
        }

        String str = new String( array );
        try {
            str = str.substring( offset, offset + count);
        } catch (StringIndexOutOfBoundsException e ) {
            e.printStackTrace();
            System.exit( -1 );
        }

        StringTokenizer tokenizer = new StringTokenizer( str, Builder.getDelimitersStr(), true);
        int tokS = 0;
        int tokLen = 0;
        int defaultTokenType = Token.IDENTIFIER;

        while ( tokenizer.hasMoreTokens() ) {
            tokS += tokLen;
            String tok = tokenizer.nextToken();
            tokLen = tok.length();
            int currentTokenStart = offset + tokS;
            int tokenType = defaultTokenType;

            if ( tok.indexOf("#") == 0 ) {
                while ( tokenizer.hasMoreTokens() ) {
                    tok = tokenizer.nextToken();
                    tokLen += tok.length();
                }
                tokenType = Token.MARKUP_COMMENT;
            } else if ( tok.equals("\n") ) {
//                    System.out.println("!!!!EOL");
                addNullToken();
                return firstToken;
            } else  if ( tok.equals("\"") ) {
                boolean slash = false;
                while ( tokenizer.hasMoreTokens() ) {
                    tok = tokenizer.nextToken();
                    tokLen += tok.length();

                    if ( tok.equals("\"") ) {
                        if ( !slash )
                            break;
                    }
                    slash = tok.equals("\\");
                }
                tokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
            } else  if ( isSpace( tok ) ) {
                tokenType = Token.WHITESPACE;
            } else  if ( Builder.getOperators().containsKey( tok ) ) {
                tokenType = Token.OPERATOR;
            } else  if ( Builder.fmapContainsName( Builder.getBfuncMap(), tok )
                    || tok.equals("init")
                    || tok.equals("run") ) {
                tokenType = Token.FUNCTION;
            } else if ( Builder.getProperties().containsKey( tok ) ) {
                tokenType = Token.MARKUP_ENTITY_REFERENCE;
            } else if ( Builder.getDataTypeSpec().containsKey( tok ) ) {
                tokenType = Token.DATA_TYPE;
            }
//            System.out.println("addToken. \"" + tok + "\"  start: " + (offset + tokS) + " startOffset: " + (newStartOffset + currentTokenStart) );
            addToken(text, currentTokenStart, currentTokenStart + tokLen - 1, tokenType , newStartOffset + currentTokenStart );
        }
        addNullToken();
        // Return the first token in our linked list.
        return firstToken;
    }

    boolean isSpace(String tok ) {
        return ( tok.equals(" ") || tok.equals("\t") );
    }
}
