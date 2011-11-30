/*
import alice.tuprolog.*;
import java.beans.*;

/**
public class ThinletConsole extends Thinlet implements OutputListener, QueryListener  {
	/** The Prolog engine referenced by the console. */
	/**
	/** The input field used to get queries. */
	/** A message describing the status of the console. */
	/** Used for components interested in changes of console's properties. */
	public ThinletConsole(IDE ide) {
	/**

	/**
	/**
	 */
	public void setInputField(InputField inputField) {
		this.inputField = inputField;
		// hopefully this will not be needed soon...
		this.inputField.setConsole(this);
	}
	/**
	 */
	public void setStatusMessage(String message) {
		String oldStatusMessage = getStatusMessage();
		statusMessage = message;
		propertyChangeSupport.firePropertyChange("StatusMessage",
	/**
	 */
	public String getStatusMessage() {
		return statusMessage;
	}
	public void addPropertyChangeListener(PropertyChangeListener listener) {
	public void removePropertyChangeListener(PropertyChangeListener listener) {
	/**
	/**
	 */

	 */
	protected void enableSolutionCommands(boolean flag) {
	protected void enableStopButton(boolean flag) {
	/**
	 */
	protected void enableSolveCommands(boolean flag) {
	/**
	/**
	public void acceptSolution() {
	public void stopEngine() {
	/* Implementing OutputListener */
	public void newQueryResultAvailable(QueryEvent event) {
} // end ThinletConsole class