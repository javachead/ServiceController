package raisetech.student.dto;

/**
 * エラーレスポンスを表すDTOクラス
 */
public record ErrorResponse(String message, String details, int status) {}