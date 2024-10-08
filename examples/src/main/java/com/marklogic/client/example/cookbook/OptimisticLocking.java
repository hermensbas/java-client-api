/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.example.cookbook;

import com.marklogic.client.*;
import com.marklogic.client.admin.ServerConfigurationManager;
import com.marklogic.client.admin.ServerConfigurationManager.UpdatePolicy;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.InputStreamHandle;
import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Optimistic Locking creates a document only when the document doesn't exist and
 * updates or deletes a document only when the document hasn't changed.
 */
public class OptimisticLocking {
  public static void main(String[] args)
    throws IOException, FailedRequestException, ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException
  {
    run(Util.loadProperties());
  }

  // install the transform and then write a transformed document
  public static void run(ExampleProperties props)
    throws IOException, FailedRequestException, ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException
  {
    System.out.println("example: "+OptimisticLocking.class.getName());
    requireOptimisticLocking(props);
    modifyDatabase(props);
    tearDownExample(props);
  }

  public static void requireOptimisticLocking(ExampleProperties props)
    throws FailedRequestException, ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException
  {
	  DatabaseClient client = Util.newAdminClient(props);

    // create a manager for the server configuration
    ServerConfigurationManager configMgr = client.newServerConfigManager();

    // read the server configuration from the database
    configMgr.readConfiguration();

    // require content versions for updates and deletes
    // use Policy.OPTIONAL to allow but not require versions
    configMgr.setUpdatePolicy(UpdatePolicy.VERSION_REQUIRED);

    // write the server configuration to the database
    configMgr.writeConfiguration();

    System.out.println("enabled optimistic locking");

    // release the client
    client.release();
  }

  public static void modifyDatabase(ExampleProperties props)
    throws IOException, ResourceNotFoundException, ForbiddenUserException, FailedRequestException
  {
    String filename = "flipper.xml";

	  DatabaseClient client = Util.newClient(props);

    // acquire the content
    InputStream docStream = Util.openStream("data"+File.separator+filename);
    if (docStream == null)
      throw new IOException("Could not read document example");

    // create a manager for writing XML documents
    XMLDocumentManager docMgr = client.newXMLDocumentManager();

    // create an identifier for the document
    String docId = "/example/"+filename;

    // provide a handle for the initial content of the document
    InputStreamHandle createHandle = new InputStreamHandle();
    createHandle.set(docStream);

    // write a new document without a version (and so, without a descriptor)
    // if the document exists, write() throws an exception
    docMgr.write(docId, createHandle);

    // create a descriptor for versions of the document
    DocumentDescriptor desc = docMgr.newDescriptor(docId);

    // provide a handle for updating the content of the document
    DOMHandle updateHandle = new DOMHandle();

    // read the document, capturing the initial version with the descriptor
    docMgr.read(desc, updateHandle);

    System.out.println("created "+docId+" as version "+desc.getVersion());

    // modify the document
    Document document = updateHandle.get();
    document.getDocumentElement().setAttribute("modified", "true");

    // update the document, specifying the current version with the descriptor
    // if the document changed after reading, write() throws an exception
    docMgr.write(desc, updateHandle);

    // get the updated version without getting the content
    desc = docMgr.exists(docId);

    System.out.println("updated "+docId+" as version "+desc.getVersion());

    // delete the document, specifying the current version with the descriptor
    // if the document changed after exists(), delete() throws an exception
    docMgr.delete(desc);

    // release the client
    client.release();
  }

  // clean up by resetting the server configuration
  public static void tearDownExample(ExampleProperties props)
    throws FailedRequestException, ResourceNotFoundException, ResourceNotResendableException, ForbiddenUserException
  {
	  DatabaseClient client = Util.newAdminClient(props);

    ServerConfigurationManager configMgr = client.newServerConfigManager();

    configMgr.readConfiguration();
    configMgr.setUpdatePolicy(UpdatePolicy.MERGE_METADATA);
    configMgr.writeConfiguration();

    client.release();
  }
}
