/*
 * Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.openjdk.jmh;

import org.openjdk.jmh.link.BinaryLinkClient;
import org.openjdk.jmh.runner.BenchmarkRecord;
import org.openjdk.jmh.runner.ForkedRunner;
import org.openjdk.jmh.runner.options.Options;

import java.io.IOException;

/**
 * Main program entry point for forked JVM instance
 *
 * @author sergey.kuksenko@oracle.com
 */
public class ForkedMain {

    /**
     * Application main entry point
     *
     * @param argv Command line arguments
     */
    public static void main(String[] argv) {
        if (argv.length == 0) {
            throw new IllegalArgumentException("Empty arguments for forked VM");
        } else {
            BinaryLinkClient link = null;
            try {
                // This assumes the exact order of arguments:
                //   1) host name to back-connect
                //   2) host port to back-connect
                //   3) benchmark to execute (saves benchmark lookup via Options)
                String hostName = argv[0];
                int hostPort = Integer.valueOf(argv[1]);
                BenchmarkRecord benchmark = new BenchmarkRecord(argv[2]);

                // establish the link to host VM and pull the options
                link = new BinaryLinkClient(hostName, hostPort);
                Options options = link.requestOptions();

                // run!
                ForkedRunner runner = new ForkedRunner(options, link);
                runner.run(benchmark);
            } catch (IOException ex) {
                throw new IllegalArgumentException(ex.getMessage());
            } catch (ClassNotFoundException ex) {
                throw new IllegalArgumentException(ex.getMessage());
            } finally {
                if (link != null) {
                    try {
                        link.close();
                    } catch (IOException e) {
                        // swallow
                    }
                }
            }
        }
    }

}
