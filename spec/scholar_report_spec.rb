require 'spec_helper'

RSpec.describe 'Scholar Report', type: :feature, js: true do
  it "works" do
    visit '/development.html'
    expect(page).to have_content('Scholar Report')
  end

  it "inserts scholar's name" do
    visit_report_for('9084042')
    expect(page).to have_content('Natalie Ammarell')
  end

  it "shows 'no data available' for art works and publications" do
    visit_report_for('9084042')
    check 'include-artisticWorks'
    art_works_section = find("#artistic-works")
    expect(art_works_section).to have_content('No data available.')
    expect(art_works_section).not_to have_selector('h3')
  end

  it "registers date filtering" do
    visit_report_for('9084042')
    find("input#start").click
    choose_date('Jan 15 2012')
    expect(page).to have_content('from 2012-01-15')

    find("input#end").click
    choose_date('Feb 22 2014')
    expect(page).to have_content('until 2014-02-22')
  end

  def visit_report_for(id)
    visit "/development.html?uri=https://scholars.duke.edu/individual/per#{id}"
  end

  def choose_date(date)
    month, day, year = date.split(/\s+/)
    find('.datepicker-years .year', text: year).click
    find('.datepicker-months .month', text: month).click
    find('.datepicker-days .day', text: day).click
  end
end
