package dev.lumora.composearch.core.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The parser's contract is "never throw, always yield something usable" — these cases
 * are the shapes real backends actually send. Add a case here when a new shape shows up.
 */
class ResponseErrorParserTest {

    private val parser = ResponseErrorParser()

    @Test
    fun `reads a plain message field`() {
        val error = parser.parse(400, """{"message":"Bad input"}""")
        assertEquals("Bad input", error.message)
    }

    @Test
    fun `prefers a per-field validation message over the envelope message`() {
        val error = parser.parse(
            422,
            """{"message":"Validation failed","errors":{"email":["Email is taken"]}}""",
        )
        assertEquals("Email is taken", error.message)
        assertEquals(listOf("Email is taken"), error.fieldErrors["email"])
    }

    @Test
    fun `digs one level into a nested error object`() {
        val error = parser.parse(500, """{"error":{"message":"Boom"}}""")
        assertEquals("Boom", error.message)
    }

    @Test
    fun `falls back to raw text when the body is not json`() {
        val error = parser.parse(502, "<html>Bad Gateway</html>")
        assertTrue(error.message.contains("Bad Gateway"))
    }

    @Test
    fun `falls back to a code-based default when the body is empty`() {
        assertEquals("Not found", parser.parse(404, null).message)
        assertEquals("Network error", parser.parse(-1, "").message)
    }

    @Test
    fun `prefers the server's own code over the http status`() {
        val error = parser.parse(400, """{"message":"nope","code":4001}""")
        assertEquals(4001, error.code)
    }
}
