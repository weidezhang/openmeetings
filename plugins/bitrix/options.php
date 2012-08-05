<?php 
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
$module_id = "openmeetings";
require_once($_SERVER["DOCUMENT_ROOT"]."/bitrix/modules/".$module_id."/include.php");
IncludeModuleLangFile($_SERVER["DOCUMENT_ROOT"]."/bitrix/modules/".$module_id."/options.php");
$OPENMEETINGS_RIGHT = $APPLICATION->GetGroupRight($module_id);
if ($OPENMEETINGS_RIGHT>="R") {

	if ($REQUEST_METHOD=="GET" && COpenmeetings::IsAdmin() && strlen($RestoreDefaults)>0 && check_bitrix_sessid()) {
		COption::RemoveOption($module_id);
		$arGROUPS = array();
		$z = CGroup::GetList($v1, $v2, array("ACTIVE" => "Y", "ADMIN" => "N"));
		while($zr = $z->Fetch()) {
			$ar = array();
			$ar["ID"] = intval($zr["ID"]);
			$ar["NAME"] = htmlspecialchars($zr["NAME"])." [<a title=\"".GetMessage("MAIN_USER_GROUP_TITLE")."\" href=\"/bitrix/admin/group_edit.php?ID=".intval($zr["ID"])."&lang=".LANGUAGE_ID."\">".intval($zr["ID"])."</a>]";
			$groups[$zr["ID"]] = "[".$zr["ID"]."] ".$zr["NAME"];
			$arGROUPS[] = $ar;
		}
		reset($arGROUPS);
		while (list(,$value) = each($arGROUPS)) {
			$APPLICATION->DelGroupRight($module_id, array($value["ID"]));
		}
	}
	
	$arAllOptions = array(
			array("URL", GetMessage("OPENMEETINGS_URL"), array("text", 64)),
			array("CONTEXT", GetMessage("OPENMEETINGS_CONTEXT"), array("text", 64)),
			array("MODULE_KEY", GetMessage("OPENMEETINGS_MODULE_KEY"), array("text", 64)),
			array("OM_USER", GetMessage("OPENMEETINGS_USER"), array("text", 64)),
			array("OM_PASSWORD", GetMessage("OPENMEETINGS_PASSWORD"), array("text", 64))
	);
	
	if($REQUEST_METHOD=="POST" && strlen($Update)>0 && COpenmeetings::IsAdmin() && check_bitrix_sessid()) {
		foreach($arAllOptions as $ar) {
			$name = $ar[0];
			$val = $$name;
			if($ar[2][0] == "checkbox" && $val != "Y") {
				$val = "N";
			}
			COption::SetOptionString($module_id, $name, $val);
		}
	}
	
	$aTabs = array(
		array("DIV" => "edit1", "TAB" => GetMessage("MAIN_TAB_SET"), "ICON" => $module_id . "_settings", "TITLE" => GetMessage("MAIN_TAB_TITLE_SET")),
		array("DIV" => "edit2", "TAB" => GetMessage("MAIN_TAB_RIGHTS"), "ICON" => $module_id . "_settings", "TITLE" => GetMessage("MAIN_TAB_TITLE_RIGHTS")),
	);
	$tabControl = new CAdminTabControl("tabControl", $aTabs);
	
	$tabControl->Begin();
	?><form method="POST" action="<?echo $APPLICATION->GetCurPage()?>?mid=<?=htmlspecialchars($mid)?>&lang=<?=LANGUAGE_ID?>"><?=bitrix_sessid_post()?><?
	$tabControl->BeginNextTab();

	if (is_array($arAllOptions)) {
		foreach($arAllOptions as $Option) {
			$val = COption::GetOptionString($module_id, $Option[0]);
			$type = $Option[2];
		?>
		<tr>
			<td valign="top"><?	if($type[0]=="checkbox")
								echo "<label for=\"".htmlspecialchars($Option[0])."\">".$Option[1]."</label>";
							else
								echo $Option[1];?>
			</td>
			<td valign="top" nowrap><?
				if($type[0]=="checkbox"):
					?><input type="checkbox" name="<?echo htmlspecialchars($Option[0])?>" id="<?echo htmlspecialchars($Option[0])?>" value="Y"<?if($val=="Y")echo" checked";?>><?
				elseif($type[0]=="text"):
					?><input type="text" size="<?echo $type[1]?>" maxlength="255" value="<?echo htmlspecialchars($val)?>" name="<?echo htmlspecialchars($Option[0])?>"><?
				elseif($type[0]=="textarea"):
					?><textarea rows="<?echo $type[1]?>" cols="<?echo $type[2]?>" name="<?echo htmlspecialchars($Option[0])?>"><?echo htmlspecialchars($val)?></textarea><?
				endif;
				?></td>
		</tr>
		<?
		}
	}
		?>
	<?$tabControl->BeginNextTab();?>
	<?require_once($_SERVER["DOCUMENT_ROOT"]."/bitrix/modules/main/admin/group_rights.php");?>
	<?$tabControl->Buttons();?>
	<script language="JavaScript">
	function RestoreDefaults()	{
		if(confirm('<?echo AddSlashes(GetMessage("MAIN_HINT_RESTORE_DEFAULTS_WARNING"))?>'))
			window.location = "<?echo $APPLICATION->GetCurPage()?>?RestoreDefaults=Y&lang=<?=LANGUAGE_ID?>&mid=<?echo urlencode($mid)?>&<?=bitrix_sessid_get()?>";
	}
	</script>
	<input <?if ($OPENMEETINGS_RIGHT<"W") echo "disabled" ?> type="submit" name="Update" value="<?=GetMessage("OPENMEETINGS_SAVE")?>">
	<input type="hidden" name="Update" value="Y">
	<input type="reset" name="reset" value="<?=GetMessage("OPENMEETINGS_RESET")?>">
	<input <?if ($OPENMEETINGS_RIGHT<"W") echo "disabled" ?> type="button" title="<?echo GetMessage("MAIN_HINT_RESTORE_DEFAULTS")?>" OnClick="RestoreDefaults();" value="<?echo GetMessage("MAIN_RESTORE_DEFAULTS")?>">
	<?$tabControl->End();?>
	</form>
<?php
}
?>
