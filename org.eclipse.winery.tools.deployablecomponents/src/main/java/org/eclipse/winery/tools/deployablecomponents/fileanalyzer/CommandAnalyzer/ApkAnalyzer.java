/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/

package org.eclipse.winery.tools.deployablecomponents.fileanalyzer.CommandAnalyzer;

import org.eclipse.winery.tools.deployablecomponents.commons.Component;

import java.util.ArrayList;
import java.util.List;

public class ApkAnalyzer implements CommandAnalyzer {
    public List<Component> analyze(String command) {
        if (!command.contains(" add ")) {
            return new ArrayList<>();
        }
        command = command.replace(Commands.Apk.asString(), "");
        String[] words = command.split("\\s");
        List<String> packages = new ArrayList<>();

        boolean skipNextWord = false;
        for (String word : words) {
            if (!skipNextWord) {
                if (word.length() > 0 && word.charAt(0) != '-') {
                    packages.add(word);
                }
                // remove RUN and "add" itself
                if (word.equals("add")) {
                    packages.clear();
                }
                if (word.equals("--repository")) {
                    skipNextWord = true;
                }
            } else {
                skipNextWord = false;
            }
        }
        return parseComponents(packages);
    }

    private List<Component> parseComponents(List<String> packages) {
        List<Component> components = new ArrayList<>();
        for (String softwarePackage : packages) {
            String version = "undefined";
            String name = softwarePackage;
            if (softwarePackage.contains("@")) {
                version = softwarePackage.substring(softwarePackage.indexOf('@') + 1);
                name = softwarePackage.substring(0, softwarePackage.indexOf('@'));
            }
            components.add(new Component(name, version, "equals"));
        }
        return components;
    }

}