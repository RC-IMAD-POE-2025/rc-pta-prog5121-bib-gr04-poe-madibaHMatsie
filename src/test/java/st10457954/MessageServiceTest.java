package st10457954;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class MessageServiceTest 
{
    private final String validRecipient1 = "+27718693002";
    private final String messagePayload1 = "Hi Mike, can you join us for dinner tonight";

    private final String invalidRecipient2 = "08575975889";
    private final String messagePayload2 = "Hi Keegan, did you receive the payment?";

    private MessageService message;

    @BeforeEach
    public void setUp() {
        MessageService.resetMessageCounterForTesting();
        message = new MessageService(validRecipient1, messagePayload1);
    }

    @Test
    public void testMessageConstructor_InitializesPropertiesCorrectly() {
        MessageService msg = new MessageService("testRecipient", "testPayload");
        assertNotNull(msg.getMessageID(), "Message ID should be automatically generated and not null.");
        assertTrue(msg.getMessageID().matches("\\d{10}"), "Message ID should consist of 10 digits.");
        assertEquals("testRecipient", msg.getMessageRecipient(), "Recipient should match the constructor argument.");
        assertEquals("testPayload", msg.getMessagePayload(), "Payload should match the constructor argument.");
        assertEquals(0, msg.getMessageIndex(), "Initial message index should be 0.");
        assertEquals("", msg.getMessageHash(), "Initial message hash should be an empty string.");
    }

    @Test
    public void testGetGeneratedIdNotification_ReturnsCorrectFormat() {
        String notification = message.getGeneratedIdNotification();
        assertTrue(notification.startsWith("Message ID generated: "), "Notification string format is incorrect.");
        assertEquals(message.getMessageID(), notification.substring("Message ID generated: ".length()), "Notification should accurately contain the generated Message ID.");
    }

    @Test
    public void testValidatePayloadLength_ValidPayload_ReturnsSuccessMessage() {
        String shortPayload = "Hello";
        assertEquals("Message ready to send.", message.validatePayloadLength(shortPayload), "Short payload should be valid.");

        String exactLengthPayload = new String(new char[250]).replace('\0', 'a');
        assertEquals("Message ready to send.", message.validatePayloadLength(exactLengthPayload), "Payload of exact maximum length (250 chars) should be valid.");
    }

    @Test
    public void testValidatePayloadLength_TooLongPayload_ReturnsFailureMessageWithExcessCount() {
        String longPayload = new String(new char[251]).replace('\0', 'a');
        assertEquals("Message exceeds 250 characters by 1, please reduce size.", message.validatePayloadLength(longPayload), "Payload exceeding max length by 1 char should report correctly.");

        String veryLongPayload = new String(new char[300]).replace('\0', 'b');
        assertEquals("Message exceeds 250 characters by 50, please reduce size.", message.validatePayloadLength(veryLongPayload), "Payload exceeding max length by 50 chars should report correctly.");
    }

    @Test
    public void testValidatePayloadLength_EmptyPayload_ReturnsSuccessMessage() {
        assertEquals("Message ready to send.", message.validatePayloadLength(""), "Empty payload should be considered valid by length check.");
    }
    
    @Test
    public void testValidatePayloadLength_NullPayload_ReturnsFailureMessage() {
        assertEquals("Message exceeds 250 characters by -250, please reduce size.", message.validatePayloadLength(null), "Null payload should result in a specific failure message.");
    }

    @Test
    public void testValidateRecipientNumber_ValidFormat_ReturnsSuccessMessage() {
        assertEquals("Cell phone number successfully captured.", message.validateRecipientNumber("+27123456789"), "Correctly formatted South African number should be valid.");
    }

    @Test
    public void testValidateRecipientNumber_InvalidFormat_ReturnsFailureMessage() {
        String expectedError = "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        assertEquals(expectedError, message.validateRecipientNumber("0712345678"), "Number missing '+27' prefix should be invalid.");
        assertEquals(expectedError, message.validateRecipientNumber("+2712345678"), "Number too short after '+27' should be invalid.");
        assertEquals(expectedError, message.validateRecipientNumber("+271234567890"), "Number too long after '+27' should be invalid.");
        assertEquals(expectedError, message.validateRecipientNumber("invalid"), "Non-numeric or improperly structured number should be invalid.");
        assertEquals(expectedError, message.validateRecipientNumber(""), "Empty string for recipient number should be invalid.");
        assertEquals(expectedError, message.validateRecipientNumber(null), "Null recipient number should be invalid.");
    }

    @Test
    public void testCreateMessageHash_WithSpecifiedData_ReturnsCorrectHash() {
        String testID = "0012345678"; 
        int testIndex = 0;
        String testPayload = "Hi Mike, can you join us for dinner tonight";
        assertEquals("00:0:HITONIGHT", message.createMessageHash(testID, testIndex, testPayload), "Hash for Test Case 1 data should be '00:0:HITONIGHT'.");

        String testID2 = "AB98765432";
        int testIndex2 = 15;
        String testPayload2 = " Hello world example ";
        assertEquals("AB:15:HELLOEXAMPLE", message.createMessageHash(testID2, testIndex2, testPayload2), "Hash with spaces in payload should correctly use trimmed words.");
    }

    @Test
    public void testSentMessage_InvalidRecipient_ReturnsFailure() {
        MessageService msg = new MessageService(invalidRecipient2, messagePayload1);
        assertEquals("Failed to send message: Invalid recipient", msg.sentMessage(), "sentMessage should return failure for an invalid recipient.");
        assertEquals(0, msg.getMessageIndex(), "Message index should remain 0 on a failed send attempt due to invalid recipient.");
        assertTrue(msg.getMessageHash().isEmpty(), "Message hash should remain empty on a failed send attempt.");
        assertEquals(0, MessageService.returnTotalMessages(), "Total messages sent counter should remain 0 after a failed send.");
    }

    @Test
    public void testSentMessage_PayloadTooLong_ReturnsFailure() {
        String longPayload = new String(new char[251]).replace('\0', 'a');
        MessageService msg = new MessageService(validRecipient1, longPayload);
        assertEquals("Failed to send message: Payload too long", msg.sentMessage(), "sentMessage should return failure for a payload exceeding maximum length.");
        assertEquals(0, MessageService.returnTotalMessages(), "Total messages sent counter should remain 0 if payload is too long.");
    }
    
    @Test
    public void testSentMessage_EmptyPayload_ReturnsFailure() {
        MessageService msg = new MessageService(validRecipient1, "   ");
        assertEquals("Failed to send message: Message content cannot be empty", msg.sentMessage(), "sentMessage should return failure for an empty payload.");
        assertEquals(0, MessageService.returnTotalMessages(), "Total messages sent counter should remain 0 if payload is empty.");
    }

    @Test
    public void testStoreMessage_Draft_CreatesFileWithIndex0(@TempDir Path tempDir) throws IOException {
        MessageService draftMessage = new MessageService(validRecipient1, "This is a draft.");
        
        String expectedFileName = "message_0.json";
        File messageFile = new File(expectedFileName);
        if (messageFile.exists()) {
            assertTrue(messageFile.delete(), "Cleanup: Failed to delete pre-existing test file.");
        }

        assertEquals("Message successfully stored.", draftMessage.storeMessage(), "storeMessage should return success for a draft.");
        assertTrue(messageFile.exists(), "JSON file for the draft message (message_0.json) should be created.");

        String content = Files.readString(messageFile.toPath());
        assertTrue(content.contains("\"MESSAGE_ID\":\"" + draftMessage.getMessageID() + "\""), "Stored JSON should contain the correct Message ID.");
        assertTrue(content.contains("\"MESSAGE_INDEX\":0"), "Stored JSON for a draft should have MESSAGE_INDEX as 0.");
        assertTrue(content.contains("\"MESSAGE_HASH\":\"\""), "Stored JSON for a draft should have an empty MESSAGE_HASH.");
        assertTrue(content.contains("\"MESSAGE_PAYLOAD\":\"This is a draft.\""), "Stored JSON should contain the correct payload.");

        assertTrue(messageFile.delete(), "Cleanup: Failed to delete test file after test completion.");
    }

    @Test
    public void testStoreMessage_SentMessage_CreatesFileWithCorrectIndexAndHash(@TempDir Path tempDir) throws IOException {
        MessageService sentMsg = new MessageService(validRecipient1, "This is a sent message.");
        sentMsg.sentMessage();

        assertNotEquals(0, sentMsg.getMessageIndex(), "Index of a sent message should not be 0.");
        assertFalse(sentMsg.getMessageHash().isEmpty(), "Hash of a sent message should not be empty.");

        String expectedFileName = "message_" + sentMsg.getMessageIndex() + ".json";
        File messageFile = new File(expectedFileName);
         if (messageFile.exists()) {
            assertTrue(messageFile.delete(), "Cleanup: Failed to delete pre-existing test file.");
        }

        assertEquals("Message successfully stored.", sentMsg.storeMessage(), "storeMessage should return success for a sent message.");
        assertTrue(messageFile.exists(), "JSON file for the sent message should be created with the correct index in filename.");

        String content = Files.readString(messageFile.toPath());
        assertTrue(content.contains("\"MESSAGE_ID\":\"" + sentMsg.getMessageID() + "\""), "Stored JSON should contain the correct Message ID.");
        assertTrue(content.contains("\"MESSAGE_INDEX\":" + sentMsg.getMessageIndex()), "Stored JSON should contain the correct MESSAGE_INDEX.");
        assertTrue(content.contains("\"MESSAGE_HASH\":\"" + sentMsg.getMessageHash() + "\""), "Stored JSON should contain the correct MESSAGE_HASH.");
        assertTrue(content.contains("\"MESSAGE_PAYLOAD\":\"This is a sent message.\""), "Stored JSON should contain the correct payload.");
        
        assertTrue(messageFile.delete(), "Cleanup: Failed to delete test file after test completion.");
    }

    @Test
    public void testReturnTotalMessages_NoMessagesSent_ReturnsZero() {
        assertEquals(0, MessageService.returnTotalMessages(), "returnTotalMessages should return 0 initially.");
    }
}