package org.entur.lahmu.domain.gbfs.v2_1

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class GBFSVersionsTest {

    @Test
    fun testDeserialize() {
        Assertions.assertDoesNotThrow {
            Json.decodeFromString<GBFSVersions>("""
            {
              "last_updated": 1434054678,
              "ttl": 0,
              "version": "2.0",
              "data": {
                "versions": [
                  {
                    "version":"1.0",
                    "url":"https://www.example.com/gbfs/1/gbfs"
                  },
                  {
                    "version":"2.0",
                    "url":"https://www.example.com/gbfs/2/gbfs"
                  }
                ]
              }
            }
            """
            )
        }
    }
}