/*
 * Copyright (c) 2017 Baidu, Inc. All Rights Reserve.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baidu.fsg.uid.utils;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DockerUtils
 *
 * @author yutianbao
 */
public final class DockerUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(DockerUtils.class);
    /**
     * Environment param keys
     */
    private static final String ENV_KEY_HOST = "JPAAS_HOST";
    private static final String ENV_KEY_PORT = "JPAAS_HTTP_PORT";
    private static final String ENV_KEY_PORT_ORIGINAL = "JPAAS_HOST_PORT_8080";
    /**
     * Docker host & port
     * -- GETTER --
     *  Retrieve docker host
     *
     * @return empty string if not a docker

     */
    @Getter
    private static String dockerHost = "";
    /**
     * -- GETTER --
     *  Retrieve docker port
     *
     * @return empty string if not a docker
     */
    @Getter
    private static String dockerPort = "";
    /**
     * Whether is docker
     */
    private static boolean isDocker;

    static {
        retrieveFromEnv();
    }

    private DockerUtils() {
    }

    /**
     * Whether a docker
     *
     * @return 是否docker
     */
    public static boolean isDocker() {
        return isDocker;
    }

    /**
     * Retrieve host & port from environment
     */
    private static void retrieveFromEnv() {
        // retrieve host & port from environment
        dockerHost = System.getenv(ENV_KEY_HOST);
        dockerPort = System.getenv(ENV_KEY_PORT);

        // not found from 'JPAAS_HTTP_PORT', then try to find from 'JPAAS_HOST_PORT_8080'
        if (StrUtil.isBlank(dockerPort)) {
            dockerPort = System.getenv(ENV_KEY_PORT_ORIGINAL);
        }

        boolean hasEnvHost = StrUtil.isNotBlank(dockerHost);
        boolean hasEnvPort = StrUtil.isNotBlank(dockerPort);

        // docker can find both host & port from environment
        if (hasEnvHost && hasEnvPort) {
            isDocker = true;

            // found nothing means not a docker, maybe an actual machine
        } else if (!hasEnvHost && !hasEnvPort) {
            isDocker = false;

        } else {
            LOGGER.error("Missing host or port from env for Docker. host:{}, port:{}", dockerHost, dockerPort);
            throw new RuntimeException(
                    "Missing host or port from env for Docker. host:" + dockerHost + ", port:" + dockerPort);
        }
    }

}
