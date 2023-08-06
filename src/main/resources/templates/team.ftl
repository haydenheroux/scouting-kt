<#-- @ftlvariable name="team" type="io.github.haydenheroux.scouting.models.team.Team" -->
<#import "_layout.ftl" as layout />
<@layout.header>
    <@layout.team_header team=team />
    <#list team.seasons as season>
        <@layout.season_section season=season />
    </#list>
</@layout.header>
