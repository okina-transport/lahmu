package org.entur.lahmu.domain.gbfs.v2_1

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SystemInformationTest {

    @Test
    fun testDeserialize() {
        Assertions.assertDoesNotThrow {
            Json.decodeFromString<SystemInformation>("""
            {
              "last_updated": 1434054678,
              "ttl": 0,
              "version": "2.0",
              "data": {
                "system_id": "test_system",
                "language": "en",
                "name": "Test system",
                "short_name": "Test",
                "operator": "Test operator",
                "url": "https://test.com",
                "purchase_url": "https://test.com/buy",
                "start_date": "2020-01-01",
                "phone_number": "1-800-TEST",
                "email": "info@test.com",
                "feed_contact_email": "feed@test.com",
                "timezone": "America/Los_Angeles",
                "license_url": "https://test.com/license",
                "rental_apps": {
                    "android": {
                        "store_uri": "android://test_store",
                        "discovery_uri": "android://discovery/test"
                    },
                    "ios": {
                        "store_uri": "ios://test_store",
                        "discovery_uri": "ios://discovery/test"
                    }
                }
              }
            }
            """
            )
        }
    }
}
