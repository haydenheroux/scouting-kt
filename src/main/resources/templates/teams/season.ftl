<#-- @ftlvariable name="team" type="io.github.haydenheroux.scouting.models.team.Team" -->
<#-- @ftlvariable name="season" type="io.github.haydenheroux.scouting.models.team.Season" -->
<#import "/common/layout.ftl" as layout />
<#import "/common/links.ftl" as links />
<#import "/common/sections.ftl" as sections />
<@layout.header title="${season.year?c} - Team ${team.number?c}">
    <h1><@links.team_link team=team /></h1>
    <@sections.season_section team=team season=season />
</@layout.header>
