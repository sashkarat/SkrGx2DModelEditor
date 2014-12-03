package org.skr.PhysModelEditor.PolisySourceEditor;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMaker;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;
import org.skr.gdx.policy.PhysPolicy;
import org.skr.gdx.policy.PhysPolicyBuilder;

import javax.swing.text.Segment;
import java.util.StringTokenizer;

/**
 * Created by rat on 21.11.14.
 */
public class PolicySourceTokenMaker extends AbstractTokenMaker {
    @Override
    public TokenMap getWordsToHighlight() {
        TokenMap tm = new TokenMap();
        for ( String keyStr : PhysPolicyBuilder.getKeywordsMap().keySet() ) {
            byte bc = PhysPolicyBuilder.getKeywordsMap().get( keyStr );
            int tokenType = Token.RESERVED_WORD;
            if ( PhysPolicy.isDts( bc ) ) {
                tokenType = Token.DATA_TYPE;
            } else if ( PhysPolicy.isProperty( bc ) ) {
                tokenType = Token.VARIABLE;
            } else if ( PhysPolicy.isRetCode( bc ) ) {
                tokenType = Token.REGEX;
            }
            tm.put(keyStr, tokenType );
        }
        tm.put("_TRUE", Token.REGEX);
        tm.put("_FALSE", Token.REGEX);
        return tm;
    }

    @Override
    public void addToken(Segment segment, int start, int end, int tokenType, int startOffset) {
        // This assumes all keywords, etc. were parsed as "identifiers."

        if ( end >= segment.array.length ) {
            System.err.println("PolicySourceTokenMaker.addToken(...) Wrong end: " + end +
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

        StringTokenizer tokenizer = new StringTokenizer( str, " \t\r\n;\"\\@$#", true);
        int tokS = 0;
        int tokLen = 0;
        int defaultTokenType = Token.IDENTIFIER;
        while ( tokenizer.hasMoreTokens() ) {
            tokS += tokLen;
            String tok = tokenizer.nextToken();
            tokLen = tok.length();
            int currentTokenStart = offset + tokS;

            if ( tok.indexOf("//") == 0 ) {
                while ( tokenizer.hasMoreTokens() ) {
                    tok = tokenizer.nextToken();
                    tokLen += tok.length();
                }
                addToken(text, currentTokenStart, currentTokenStart + tokLen - 1, Token.MARKUP_COMMENT, newStartOffset + currentTokenStart );
                continue;
            }
            if ( tok.length() == 1 ) {
                if ( tok.equals("\n") ) {
//                    System.out.println("!!!!EOL");
                    addNullToken();
                    return firstToken;
                }

                if ( tok.equals("\"") ) {
                    boolean slash = false;
                    while ( tokenizer.hasMoreTokens() ) {
                        tok = tokenizer.nextToken();
                        tokLen += tok.length();

                        if ( tok.equals("\"") ) {
                            if ( !slash )
                                break;
                        }
                        if ( tok.equals("\\") ) {
                            slash = true;
                        } else {
                            slash = false;
                        }
                    }
                    addToken(text, currentTokenStart, currentTokenStart + tokLen - 1, Token.LITERAL_STRING_DOUBLE_QUOTE,  newStartOffset + currentTokenStart );
                    continue;
                }

                if ( isSpace( tok ) ) {
                    addToken(text, currentTokenStart, currentTokenStart + tokLen - 1, Token.WHITESPACE,  newStartOffset + currentTokenStart );
                    continue;
                }

                if ( tok.equals("#") ) {
                    defaultTokenType = Token.PREPROCESSOR;
                }

                if ( tok.equals("@") ) {
                    if ( tokenizer.hasMoreTokens() ) {
                        tokLen += tokenizer.nextToken().length();
                    }
                    addToken(text, currentTokenStart, currentTokenStart + tokLen - 1, Token.ANNOTATION,  newStartOffset + currentTokenStart );
                    continue;
                }

                if ( tok.equals("$") ) {
                    if ( tokenizer.hasMoreTokens() ) {
                        tokLen += tokenizer.nextToken().length();
                    }
                    addToken(text, currentTokenStart, currentTokenStart + tokLen - 1, Token.MARKUP_TAG_NAME,  newStartOffset + currentTokenStart );
                    continue;
                }

            }
//            System.out.println("addToken. \"" + tok + "\"  start: " + (offset + tokS) + " startOffset: " + (newStartOffset + currentTokenStart) );
            addToken(text, currentTokenStart, currentTokenStart + tokLen - 1, defaultTokenType , newStartOffset + currentTokenStart );
        }
        addNullToken();
        // Return the first token in our linked list.
        return firstToken;
    }

    boolean isSpace(String tok ) {
        return ( tok.equals(" ") || tok.equals("\t") );
    }
}
