package logging

import (
	"log/slog"
	"os"
)

var logLevel = &slog.LevelVar{}

var options = &slog.HandlerOptions{
	Level: logLevel,
}

var errorLogger = slog.New(slog.NewJSONHandler(os.Stderr, options))

var stdLogger = slog.New(slog.NewJSONHandler(os.Stdout, options))

func Info(msg string, args ...any) {
	stdLogger.Info(msg, args...)
}

func Debug(msg string, args ...any) {
	stdLogger.Debug(msg, args...)
}

func Warn(msg string, args ...any) {
	stdLogger.Warn(msg, args...)
}

func Error(msg string, args ...any) {
	errorLogger.Error(msg, args...)
}

func Fatal(msg string, args ...any) {
	errorLogger.Error(msg, args...)
	os.Exit(1)
}

func SetLevel(level slog.Level) {
	logLevel.Set(level)
}
