package org.entur.lahmu.domain.gbfs.v2_2

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SystemHoursTest {
    @Test
    fun testDeserialize() {
        Assertions.assertDoesNotThrow {
            val json = """
                {
                    "last_updated": 1434054678,
                    "ttl": 0,
                    "version": "2.0",
                    "data": {
                        "rental_hours": [
                            {
                                "user_types": [ "member" ],
                                "days": ["sat", "sun"],
                                "start_time": "00:00:00",
                                "end_time": "23:59:59"
                            },
                            {
                                "user_types": [ "nonmember" ],
                                "days": ["sat", "sun"],
                                "start_time": "05:00:00",
                                "end_time": "23:59:59"
                            },
                            {
                                "user_types": [ "member", "nonmember" ],
                                "days": ["mon", "tue", "wed", "thu", "fri"],
                                "start_time": "00:00:00",
                                "end_time": "23:59:59"
                            }
                        ]
                    }   
                }
                """.replace("\\s".toRegex(), "")
            val systemHours = Json.decodeFromString<SystemHours>(json)
            Assertions.assertEquals(json, Json.encodeToString(systemHours))
        }
    }
}
