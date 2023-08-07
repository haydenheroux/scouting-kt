<#-- @ftlvariable name="teamReference" type="io.github.haydenheroux.scouting.models.team.Team" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <@layout.team_header team=teamReference />
    <#list teamReference.seasonReferences as seasonReference>
        <@layout.season_section season=seasonReference />
    </#list>
</@layout.header>
