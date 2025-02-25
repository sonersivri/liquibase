package liquibase.extension.testing.command

import liquibase.exception.CommandValidationException

CommandTests.define {
    command = ["updateToTag"]
    signature = """
Short Description: Deploy changes from the changelog file to the specified tag
Long Description: NOT SET
Required Args:
  changelogFile (String) The root changelog
  tag (String) The tag to update to
  url (String) The JDBC database connection URL
    OBFUSCATED
Optional Args:
  changeExecListenerClass (String) Fully-qualified class which specifies a ChangeExecListener
    Default: null
  changeExecListenerPropertiesFile (String) Path to a properties file for the ChangeExecListenerClass
    Default: null
  contexts (String) Changeset contexts to match
    Default: null
  defaultCatalogName (String) The default catalog name to use for the database connection
    Default: null
  defaultSchemaName (String) The default schema name to use for the database connection
    Default: null
  driver (String) The JDBC driver class
    Default: null
  driverPropertiesFile (String) The JDBC driver properties file
    Default: null
  labelFilter (String) Changeset labels to match
    Default: null
  password (String) Password to use to connect to the database
    Default: null
    OBFUSCATED
  username (String) Username to use to connect to the database
    Default: null
"""

    run "Happy path", {
        arguments = [
                url:        { it.url },
                username:   { it.username },
                password:   { it.password },
                tag          : "version_2.0",
                changelogFile: "changelogs/h2/complete/simple.tag.changelog.xml",
        ]


        expectedResults = [
                statusCode   : 0
        ]
    }

    run "Mismatched DBMS causes not deployed summary message", {
        arguments = [
                url:        { it.url },
                username:   { it.username },
                password:   { it.password },
                tag          : "version_2.0",
                showSummary  : "verbose",
                changelogFile: "changelogs/h2/complete/mismatchedDbms.changelog.xml"
        ]

        expectedResults = [
                statusCode   : 0
        ]

        expectedUI = [
"""
UPDATE SUMMARY
Run:                          2
Previously run:               0
DBMS mismatch:                1
Not in filter:                0
-------------------------------
Total change sets:            3

+--------------------------------------------------------------+--------------------------------+
| Changeset Info                                               | Reason Skipped                 |
+--------------------------------------------------------------+--------------------------------+
|                                                              | mismatched DBMS value of 'foo' |
| changelogs/h2/complete/mismatchedDbms.changelog.xml::1::nvox |                                |
| land                                                         |                                |
+--------------------------------------------------------------+--------------------------------+
"""
        ]
    }
    run "Run without a tag throws an exception", {
        arguments = [
                url          : "",
                changelogFile: "changelogs/h2/complete/simple.tag.changelog.xml",
        ]
        expectedException = CommandValidationException.class
    }

    run "Run without a changeLogFile throws an exception", {
        arguments = [
                url          : "",
                tag          : "version_2.0",
        ]
        expectedException = CommandValidationException.class
    }

    run "Run without URL throws an exception", {
        arguments = [
                url          : "",
                tag          : "version_2.0",
                changelogFile: "changelogs/h2/complete/simple.tag.changelog.xml",
        ]
        expectedException = CommandValidationException.class
    }

    run "Run with a bad show summary option throws an exception", {
        arguments = [
                url                    : { it.url },
                username               : { it.username },
                password               : { it.password },
                changelogFile          : 'changelogs/h2/complete/simple.changelog.xml',
                showSummary: "foo",
                tag          : "version_2.0"
        ]
        expectedException = IllegalArgumentException.class
    }

    run "Run without any arguments throws an exception", {
        arguments = [
                url          : "",
        ]
        expectedException = CommandValidationException.class
    }
}
