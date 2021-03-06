// Generated from org/linqs/psl/parser/antlr/PSL.g4 by ANTLR 4.7.1
package org.linqs.psl.parser.antlr;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PSLLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, STRING_LITERAL=5, EXPONENT_EXPRESSION=6, 
		LESS_THAN_EQUAL=7, GREATER_THAN_EQUAL=8, EQUAL=9, PLUS=10, MINUS=11, MULT=12, 
		DIV=13, MAX=14, MIN=15, IDENTIFIER=16, NONNEGATIVE_NUMBER=17, PERIOD=18, 
		COMMA=19, COLON=20, NEGATION=21, AMPERSAND=22, PIPE=23, LPAREN=24, RPAREN=25, 
		LBRACE=26, RBRACE=27, LBRACKET=28, RBRACKET=29, SINGLE_QUOTE=30, DOUBLE_QUOTE=31, 
		MOD=32, CARROT=33, WS=34, COMMENT=35, LINE_COMMENT=36, PYTHON_COMMENT=37;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "STRING_LITERAL", "STANDARD_STRING_ESCAPE", 
		"EXPONENT_EXPRESSION", "LESS_THAN_EQUAL", "GREATER_THAN_EQUAL", "EQUAL", 
		"PLUS", "MINUS", "MULT", "DIV", "MAX", "MIN", "IDENTIFIER", "NONNEGATIVE_NUMBER", 
		"LETTER", "DIGIT", "PERIOD", "COMMA", "COLON", "NEGATION", "AMPERSAND", 
		"PIPE", "LPAREN", "RPAREN", "LBRACE", "RBRACE", "LBRACKET", "RBRACKET", 
		"SINGLE_QUOTE", "DOUBLE_QUOTE", "MOD", "CARROT", "WS", "COMMENT", "LINE_COMMENT", 
		"PYTHON_COMMENT"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'>>'", "'->'", "'<<'", "'<-'", null, null, "'<='", "'>='", "'='", 
		"'+'", "'-'", "'*'", "'/'", "'@Max'", "'@Min'", null, null, "'.'", "','", 
		"':'", null, "'&'", "'|'", "'('", "')'", "'{'", "'}'", "'['", "']'", "'''", 
		"'\"'", "'%'", "'^'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, "STRING_LITERAL", "EXPONENT_EXPRESSION", 
		"LESS_THAN_EQUAL", "GREATER_THAN_EQUAL", "EQUAL", "PLUS", "MINUS", "MULT", 
		"DIV", "MAX", "MIN", "IDENTIFIER", "NONNEGATIVE_NUMBER", "PERIOD", "COMMA", 
		"COLON", "NEGATION", "AMPERSAND", "PIPE", "LPAREN", "RPAREN", "LBRACE", 
		"RBRACE", "LBRACKET", "RBRACKET", "SINGLE_QUOTE", "DOUBLE_QUOTE", "MOD", 
		"CARROT", "WS", "COMMENT", "LINE_COMMENT", "PYTHON_COMMENT"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public PSLLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "PSL.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\'\u010d\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\3\2\3\2\3\2\3"+
		"\3\3\3\3\3\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6\7\6c\n\6\f\6\16\6f\13\6"+
		"\3\6\3\6\3\6\3\6\3\6\7\6m\n\6\f\6\16\6p\13\6\3\6\3\6\5\6t\n\6\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7\u0082\n\7\3\b\3\b\3\b\3\t"+
		"\3\t\3\t\3\n\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17\3\20"+
		"\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\7\22\u00a4"+
		"\n\22\f\22\16\22\u00a7\13\22\3\23\6\23\u00aa\n\23\r\23\16\23\u00ab\3\23"+
		"\3\23\6\23\u00b0\n\23\r\23\16\23\u00b1\5\23\u00b4\n\23\3\23\3\23\5\23"+
		"\u00b8\n\23\3\23\6\23\u00bb\n\23\r\23\16\23\u00bc\5\23\u00bf\n\23\3\24"+
		"\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33"+
		"\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3#\3"+
		"#\3$\3$\3%\3%\3&\6&\u00e6\n&\r&\16&\u00e7\3&\3&\3\'\3\'\3\'\3\'\7\'\u00f0"+
		"\n\'\f\'\16\'\u00f3\13\'\3\'\3\'\3\'\3\'\3\'\3(\3(\3(\3(\7(\u00fe\n(\f"+
		"(\16(\u0101\13(\3(\3(\3)\3)\7)\u0107\n)\f)\16)\u010a\13)\3)\3)\3\u00f1"+
		"\2*\3\3\5\4\7\5\t\6\13\7\r\2\17\b\21\t\23\n\25\13\27\f\31\r\33\16\35\17"+
		"\37\20!\21#\22%\23\'\2)\2+\24-\25/\26\61\27\63\30\65\31\67\329\33;\34"+
		"=\35?\36A\37C E!G\"I#K$M%O&Q\'\3\2\13\4\2))^^\4\2$$^^\3\2\63\64\4\2GG"+
		"gg\6\2&&C\\aac|\3\2\62;\4\2##\u0080\u0080\5\2\13\f\16\17\"\"\4\2\f\f\17"+
		"\17\2\u011f\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2"+
		"\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2"+
		"\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2"+
		"\2\2%\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2"+
		"\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2"+
		"\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2"+
		"M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\3S\3\2\2\2\5V\3\2\2\2\7Y\3\2\2\2\t\\\3"+
		"\2\2\2\13s\3\2\2\2\r\u0081\3\2\2\2\17\u0083\3\2\2\2\21\u0086\3\2\2\2\23"+
		"\u0089\3\2\2\2\25\u008c\3\2\2\2\27\u008e\3\2\2\2\31\u0090\3\2\2\2\33\u0092"+
		"\3\2\2\2\35\u0094\3\2\2\2\37\u0096\3\2\2\2!\u009b\3\2\2\2#\u00a0\3\2\2"+
		"\2%\u00a9\3\2\2\2\'\u00c0\3\2\2\2)\u00c2\3\2\2\2+\u00c4\3\2\2\2-\u00c6"+
		"\3\2\2\2/\u00c8\3\2\2\2\61\u00ca\3\2\2\2\63\u00cc\3\2\2\2\65\u00ce\3\2"+
		"\2\2\67\u00d0\3\2\2\29\u00d2\3\2\2\2;\u00d4\3\2\2\2=\u00d6\3\2\2\2?\u00d8"+
		"\3\2\2\2A\u00da\3\2\2\2C\u00dc\3\2\2\2E\u00de\3\2\2\2G\u00e0\3\2\2\2I"+
		"\u00e2\3\2\2\2K\u00e5\3\2\2\2M\u00eb\3\2\2\2O\u00f9\3\2\2\2Q\u0104\3\2"+
		"\2\2ST\7@\2\2TU\7@\2\2U\4\3\2\2\2VW\7/\2\2WX\7@\2\2X\6\3\2\2\2YZ\7>\2"+
		"\2Z[\7>\2\2[\b\3\2\2\2\\]\7>\2\2]^\7/\2\2^\n\3\2\2\2_d\5C\"\2`c\5\r\7"+
		"\2ac\n\2\2\2b`\3\2\2\2ba\3\2\2\2cf\3\2\2\2db\3\2\2\2de\3\2\2\2eg\3\2\2"+
		"\2fd\3\2\2\2gh\5C\"\2ht\3\2\2\2in\5E#\2jm\5\r\7\2km\n\3\2\2lj\3\2\2\2"+
		"lk\3\2\2\2mp\3\2\2\2nl\3\2\2\2no\3\2\2\2oq\3\2\2\2pn\3\2\2\2qr\5E#\2r"+
		"t\3\2\2\2s_\3\2\2\2si\3\2\2\2t\f\3\2\2\2uv\7^\2\2v\u0082\7^\2\2wx\7^\2"+
		"\2x\u0082\7)\2\2yz\7^\2\2z\u0082\7$\2\2{|\7^\2\2|\u0082\7v\2\2}~\7^\2"+
		"\2~\u0082\7p\2\2\177\u0080\7^\2\2\u0080\u0082\7t\2\2\u0081u\3\2\2\2\u0081"+
		"w\3\2\2\2\u0081y\3\2\2\2\u0081{\3\2\2\2\u0081}\3\2\2\2\u0081\177\3\2\2"+
		"\2\u0082\16\3\2\2\2\u0083\u0084\5I%\2\u0084\u0085\t\4\2\2\u0085\20\3\2"+
		"\2\2\u0086\u0087\7>\2\2\u0087\u0088\7?\2\2\u0088\22\3\2\2\2\u0089\u008a"+
		"\7@\2\2\u008a\u008b\7?\2\2\u008b\24\3\2\2\2\u008c\u008d\7?\2\2\u008d\26"+
		"\3\2\2\2\u008e\u008f\7-\2\2\u008f\30\3\2\2\2\u0090\u0091\7/\2\2\u0091"+
		"\32\3\2\2\2\u0092\u0093\7,\2\2\u0093\34\3\2\2\2\u0094\u0095\7\61\2\2\u0095"+
		"\36\3\2\2\2\u0096\u0097\7B\2\2\u0097\u0098\7O\2\2\u0098\u0099\7c\2\2\u0099"+
		"\u009a\7z\2\2\u009a \3\2\2\2\u009b\u009c\7B\2\2\u009c\u009d\7O\2\2\u009d"+
		"\u009e\7k\2\2\u009e\u009f\7p\2\2\u009f\"\3\2\2\2\u00a0\u00a5\5\'\24\2"+
		"\u00a1\u00a4\5\'\24\2\u00a2\u00a4\5)\25\2\u00a3\u00a1\3\2\2\2\u00a3\u00a2"+
		"\3\2\2\2\u00a4\u00a7\3\2\2\2\u00a5\u00a3\3\2\2\2\u00a5\u00a6\3\2\2\2\u00a6"+
		"$\3\2\2\2\u00a7\u00a5\3\2\2\2\u00a8\u00aa\5)\25\2\u00a9\u00a8\3\2\2\2"+
		"\u00aa\u00ab\3\2\2\2\u00ab\u00a9\3\2\2\2\u00ab\u00ac\3\2\2\2\u00ac\u00b3"+
		"\3\2\2\2\u00ad\u00af\5+\26\2\u00ae\u00b0\5)\25\2\u00af\u00ae\3\2\2\2\u00b0"+
		"\u00b1\3\2\2\2\u00b1\u00af\3\2\2\2\u00b1\u00b2\3\2\2\2\u00b2\u00b4\3\2"+
		"\2\2\u00b3\u00ad\3\2\2\2\u00b3\u00b4\3\2\2\2\u00b4\u00be\3\2\2\2\u00b5"+
		"\u00b7\t\5\2\2\u00b6\u00b8\5\31\r\2\u00b7\u00b6\3\2\2\2\u00b7\u00b8\3"+
		"\2\2\2\u00b8\u00ba\3\2\2\2\u00b9\u00bb\5)\25\2\u00ba\u00b9\3\2\2\2\u00bb"+
		"\u00bc\3\2\2\2\u00bc\u00ba\3\2\2\2\u00bc\u00bd\3\2\2\2\u00bd\u00bf\3\2"+
		"\2\2\u00be\u00b5\3\2\2\2\u00be\u00bf\3\2\2\2\u00bf&\3\2\2\2\u00c0\u00c1"+
		"\t\6\2\2\u00c1(\3\2\2\2\u00c2\u00c3\t\7\2\2\u00c3*\3\2\2\2\u00c4\u00c5"+
		"\7\60\2\2\u00c5,\3\2\2\2\u00c6\u00c7\7.\2\2\u00c7.\3\2\2\2\u00c8\u00c9"+
		"\7<\2\2\u00c9\60\3\2\2\2\u00ca\u00cb\t\b\2\2\u00cb\62\3\2\2\2\u00cc\u00cd"+
		"\7(\2\2\u00cd\64\3\2\2\2\u00ce\u00cf\7~\2\2\u00cf\66\3\2\2\2\u00d0\u00d1"+
		"\7*\2\2\u00d18\3\2\2\2\u00d2\u00d3\7+\2\2\u00d3:\3\2\2\2\u00d4\u00d5\7"+
		"}\2\2\u00d5<\3\2\2\2\u00d6\u00d7\7\177\2\2\u00d7>\3\2\2\2\u00d8\u00d9"+
		"\7]\2\2\u00d9@\3\2\2\2\u00da\u00db\7_\2\2\u00dbB\3\2\2\2\u00dc\u00dd\7"+
		")\2\2\u00ddD\3\2\2\2\u00de\u00df\7$\2\2\u00dfF\3\2\2\2\u00e0\u00e1\7\'"+
		"\2\2\u00e1H\3\2\2\2\u00e2\u00e3\7`\2\2\u00e3J\3\2\2\2\u00e4\u00e6\t\t"+
		"\2\2\u00e5\u00e4\3\2\2\2\u00e6\u00e7\3\2\2\2\u00e7\u00e5\3\2\2\2\u00e7"+
		"\u00e8\3\2\2\2\u00e8\u00e9\3\2\2\2\u00e9\u00ea\b&\2\2\u00eaL\3\2\2\2\u00eb"+
		"\u00ec\7\61\2\2\u00ec\u00ed\7,\2\2\u00ed\u00f1\3\2\2\2\u00ee\u00f0\13"+
		"\2\2\2\u00ef\u00ee\3\2\2\2\u00f0\u00f3\3\2\2\2\u00f1\u00f2\3\2\2\2\u00f1"+
		"\u00ef\3\2\2\2\u00f2\u00f4\3\2\2\2\u00f3\u00f1\3\2\2\2\u00f4\u00f5\7,"+
		"\2\2\u00f5\u00f6\7\61\2\2\u00f6\u00f7\3\2\2\2\u00f7\u00f8\b\'\3\2\u00f8"+
		"N\3\2\2\2\u00f9\u00fa\7\61\2\2\u00fa\u00fb\7\61\2\2\u00fb\u00ff\3\2\2"+
		"\2\u00fc\u00fe\n\n\2\2\u00fd\u00fc\3\2\2\2\u00fe\u0101\3\2\2\2\u00ff\u00fd"+
		"\3\2\2\2\u00ff\u0100\3\2\2\2\u0100\u0102\3\2\2\2\u0101\u00ff\3\2\2\2\u0102"+
		"\u0103\b(\3\2\u0103P\3\2\2\2\u0104\u0108\7%\2\2\u0105\u0107\n\n\2\2\u0106"+
		"\u0105\3\2\2\2\u0107\u010a\3\2\2\2\u0108\u0106\3\2\2\2\u0108\u0109\3\2"+
		"\2\2\u0109\u010b\3\2\2\2\u010a\u0108\3\2\2\2\u010b\u010c\b)\3\2\u010c"+
		"R\3\2\2\2\25\2bdlns\u0081\u00a3\u00a5\u00ab\u00b1\u00b3\u00b7\u00bc\u00be"+
		"\u00e7\u00f1\u00ff\u0108\4\2\3\2\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}