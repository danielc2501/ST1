// Guhan Sundaram s1404610
// Daniel Clemente s1333465

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import st.EntryMap;
import st.TemplateEngine;

public class Task1 {

    private EntryMap map;

    private TemplateEngine engine;

    @Before
    public void setUp() throws Exception {
        map = new EntryMap();
        engine = new TemplateEngine();
    }

 

    //===========================|
    //        EntryMap TESTS     |
    //===========================|

    
    // Storing a template as empty throws an error.
    @Test(expected = Exception.class)
    public void Spec1_Entrymap_Test() {
        map.store("", "Adam", false); // Name is empty value
    }

    // Storing a template as null throws an error.
    @Test(expected = Exception.class)
    public void Spec1_Entrymap_Test2() {
        map.store(null, "Adam", false); // Name is NULL value
    }


    // Storing a Replacement string as null throws an error.
    @Test(expected = Exception.class)
    public void Spec2_Entrymap_Test() {
        map.store("name", null, false);
    }

    // Storing a case sensitive flag as null does not throw an error and defaults to being case insensitive
    // NB: seems to fail for given program
    @Test
    public void Spec3_Entrymap_Test() {
        map.store("nAmE", "Adam", null);
        String result = engine.evaluate("Hello ${name}", map,"delete-unmatched");
        assertEquals("Hello Adam", result);
    }
    

    // The entries are ordered and follow the order in which they appear in the program.
    @Test
    public void Spec4_Entrymap_Test() {
        map.store("name", "Adam", true);
        map.store("Name", "Dykes", false);
        String result = engine.evaluate("Hello ${name} ${Name}", map,"delete-unmatched");
        assertEquals("Hello Adam Dykes", result);
    }
    
    // Entries that already exist cannot be stored again
    @Test
    public void Spec5_Entrymap_Test() {
        map.store("name", "Adam", false);
        map.store("name", "Adam", false);
        assertEquals(map.getEntries().size(), 1);
    }

    //===========================|
    //        ENGINE TESTS       |
    //===========================|

    
    // Tests if the unchanged template string is output when template string is empty
    @Test
    public void Spec1_Engine_Test() {
        String result = engine.evaluate("", map,"delete-unmatched");
        assertEquals("" , result);
    }
    
    // Tests if the unchanged template string is output when template string is null
    @Test
    public void Spec1_Engine_Test2() {
        String result = engine.evaluate(null, map,"delete-unmatched");
        assertEquals(null , result);
    }


    // Tests if EntryMap object can be NULL and then the unchanged template string is returned
    @Test
    public void Spec2_Engine_Test() {
    	String result = engine.evaluate("Hello ${name}", null,"delete-unmatched");
      assertEquals("Hello ${name}", result);
    }

    // Matching mode cannot be NULL and must be one of the possible values ("keep-unmatched" and "delete-unmatched").
    // Tests if matching mode NULL or other value, it defaults to "delete-unmatched".    
    @Test
    public void Spec3_Engine_Test_null() {
    	map.store("name", "Adam", false);
        map.store("surname", "Dykes", false);    	
    	String result = engine.evaluate("Hello ${name} ${surname}, you are ${test}", map ,null);
    	assertEquals("Hello Adam Dykes, you are ", result);
    }
    
    // Tests if matching mode any other value, it defaults to "delete-unmatched".    
    @Test
    public void Spec3_Engine_Test_empty() {
    	map.store("name", "Adam", false);
        map.store("surname", "Dykes", false);    	
    	String result = engine.evaluate("Hello ${name} ${surname}, you are ${test}", map ,"");
    	assertEquals("Hello Adam Dykes, you are ", result);
    }
    

    // Tests if everything between a template's boundaries ("${" and "}") is treated as normal text when matched against an entry.
    @Test
    public void Spec4_Engine_Test() {
        map.store("$", "Adam", false);
        map.store("\n", "80", false);
        map.store("aaaaaaaaaa bbbbbbbbbbb", "85", false);
        String result = engine.evaluate("Hello ${$} ${\n} ${aaaaaaaaaa bbbbbbbbbbb}", map,"delete-unmatched");
        assertEquals("Hello Adam 80 85", result);
    }

    // Tests if when a template is matched against an entry key, any non visible character does not affect the result.
    @Test
    public void Spec5_Engine_Test() {
        map.store("name", "Adam", false);
        String result = engine.evaluate("Hello ${ n   a m    e}", map,"delete-unmatched");
        assertEquals("Hello Adam", result);
    }

    // Tests if in a template string every "${" and "}" occurrence acts as a boundary of at MOST one template.
    @Test
    public void Spec6_Engine_Test() {
        map.store("name", "Adam", false);
        map.store("competition", "comp", false);
        map.store("winning the comp cup.", "bigstring", false);
        String result = engine.evaluate("I heard that }: ${name} said: ${winning the ${competition} cup.}", map,"delete-unmatched");
        assertEquals("I heard that }: Adam said: bigstring", result);
    }

    // Tests if in a template string the different templates are ordered according to their length. The shorter templates precede.
    @Test
    public void Spec7_Engine_Test() {
    	
    	// Shorter templates should be processed first.
    	
    	// ${T} is replaced by "er"
    	
    	// Test templates are
    	// ${test${T}}
    	
    	// ${test} is replaced by "fail"
    	// ${tester} is replaced by "pass"
    	// ${failer} is replaced by failure
    	
    	// So evaluating the string "${test${T}}" should result in "pass" whereas had it processed the longer string of test,
    	// the output would have been "failure"
    	
    	
        map.store("T", "er", false);
        map.store("tester", "pass", false);
        map.store("test", "fail", false);
        map.store("failer", "failure", false);
        String result = engine.evaluate("${test${T}}", map,"delete-unmatched");
        assertEquals("pass", result);
    }

    @Test
    public void Spec8_Engine_Test() {
    	map.store("boy", "Guhan", false);
    	map.store("passion", "programming", false);
    	map.store("loves", "assembly", false);
    	String result = engine.evaluate("${boy} loves ${passion} especially in ${loves}.", map,"delete-unmatched");
        assertEquals("Guhan loves programming especially in assembly.", result);
    }


}
