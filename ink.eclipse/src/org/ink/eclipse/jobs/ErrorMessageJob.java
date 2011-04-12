package org.ink.eclipse.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.ink.eclipse.InkPlugin;

public class ErrorMessageJob extends Job {

	private String msg;

	public ErrorMessageJob(String msg) {
		super("Ink Editor");
		this.msg = msg;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		return new Status(IStatus.ERROR, InkPlugin.PLUGIN_ID, 0, msg, null);
	}
}
