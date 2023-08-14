package com.dws.challenge.service.watcher;

import java.util.Optional;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

public class CommonServiceWatcher implements TestWatcher {

	@Override
	public void testDisabled(ExtensionContext context, Optional<String> reason) {
		System.out.println("Test Disabled: " + context.getDisplayName());
		reason.ifPresent(r -> System.out.println("Reason: " + r));
	}

	@Override
	public void testSuccessful(ExtensionContext context) {
		System.out.println("Test Passed: " + context.getDisplayName());
	}

	@Override
	public void testAborted(ExtensionContext context, Throwable cause) {
		System.err.println("Test Aborted: " + context.getDisplayName());
		cause.printStackTrace();
	}

	@Override
	public void testFailed(ExtensionContext context, Throwable cause) {
		System.err.println("Test Failed: " + context.getDisplayName());
		cause.printStackTrace();
	}
}
