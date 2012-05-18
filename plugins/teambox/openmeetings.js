/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
*/

(function () {
    // Initialize a basic Teambox app
    Teambox.Apps.Openmeetings = Teambox.BaseApp.extend({});

    // Router to handle paths (in this case, /#!/openmeetings) with regular links
    Teambox.Apps.Openmeetings.Controller = Teambox.Controllers.BaseController.extend({
        routes: { '!/openmeetings'  : 'index' },
        index: function () {
            // Helper Teambox function to highlight the sidebar
            Teambox.Views.Sidebar.highlightSidebar('openmeetings_link');

            // Helper Teambox function to render content to the main area
            Teambox.helpers.views.renderContent(
                "<iframe src='protocol://host/teambox/index.php?action=authorize' style='width:100%;height:100%'></iframe>",
                "Openmeetings",
                "openmeetings_content",
                false,
                true
            );
        }
    });

    // Init app. Will be executed once on pageload when apps are instantiated.
    Teambox.Apps.Openmeetings = Teambox.Apps.Openmeetings.extend({
        initialize: function () {
            // Instantiate the controller
            this.controller = new Teambox.Apps.Openmeetings.Controller();
            // Add an element to the Apps section in the sidebar
            $(".more_section .separator")
                .after("<div class='el' id='openmeetings_link'>"+
                "<a href='#!/openmeetings'><span class='icon star'></span>Openmeetings</a>"+
                "</div>");
        }
    });

}());
