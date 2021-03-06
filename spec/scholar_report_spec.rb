require 'spec_helper'

RSpec.describe 'Scholar Report', type: :feature, js: true do
  it "works" do
    visit '/development.html'
    expect(page).to have_content('Scholar Report')
  end

  context "for person with only appointments" do
    let(:appointments_only_scholar) {'9084042'}
    before do
      visit_report_for(appointments_only_scholar)
    end

    it "inserts scholar's name" do
      expect(page).to have_content('Natalie Ammarell')
    end

    it "turns empty sections off" do
      expect(find('#include-positions')).to be_checked
      expect(find('#include-overview')).not_to be_checked
      expect(find('#include-mentorship')).not_to be_checked
      expect(find('#include-awards')).not_to be_checked
      expect(find('#include-geographicalFocus')).not_to be_checked
      expect(find('#include-courses')).not_to be_checked
      expect(find('#include-grants')).not_to be_checked
      expect(find('#include-professionalActivities')).not_to be_checked
      expect(find('#include-artisticWorks')).not_to be_checked
      expect(find('#include-publications')).not_to be_checked

      expect(find('#report')).to     have_selector('div#appointments')
      expect(find('#report')).not_to have_selector('div#overview')
      expect(find('#report')).not_to have_selector('div#mentorship')
      expect(find('#report')).not_to have_selector('div#awards')
      expect(find('#report')).not_to have_selector('div#geographical-focus')
      expect(find('#report')).not_to have_selector('div#courses')
      expect(find('#report')).not_to have_selector('div#grants')
      expect(find('#report')).not_to have_selector('div#professionalActivities')
      expect(find('#report')).not_to have_selector('div#artistic-works')
      expect(find('#report')).not_to have_selector('div#publications')
    end

    it "allows user to toggle report sections" do
      uncheck 'include-positions'
      expect(find('#report')).not_to have_selector('div#appointments')
      check 'include-positions'
      expect(find('#report')).to have_selector('div#appointments')

      check 'include-grants'
      expect(find('#report')).to have_selector('div#grants')
      uncheck 'include-grants'
      expect(find('#report')).not_to have_selector('div#grants')
    end

    it "shows 'no data available' for art, pubs, grants" do
      check 'include-artisticWorks'
      art_works_section = find("#artistic-works")
      expect(art_works_section).not_to have_selector('h3')
      expect(art_works_section.find('span').text).to eq('No data available.')

      check 'include-publications'
      pub_section = find("#publications")
      expect(pub_section).not_to have_selector('h3')
      expect(pub_section.find('span').text).to eq('No data available.')

      check 'include-grants'
      grant_section = find("#grants")
      expect(grant_section).not_to have_selector('h3')
      expect(grant_section.find('li').text).to eq('No data available.')
    end

    it "registers date filtering" do
      find("input#start").click
      choose_date('Jan 15 2012')
      expect(page).to have_content('from 2012-01-15')

      find("input#end").click
      choose_date('Feb 22 2014')
      expect(page).to have_content('until 2014-02-22')
    end
  end

  context 'for person with one publication' do
    let(:one_pub_scholar) {'0053482'}
    # Sparql to find such a person if needed, since hitting live data
    #SELECT (COUNT(?authorship) AS ?count) ?person
    #WHERE
    #{
          #?person vivo:authorInAuthorship ?authorship.
    #}
    #GROUP BY ?person ORDER BY ?count
    #LIMIT 1

    before do
      visit_report_for(one_pub_scholar)
    end

    it 'displays publication section' do
      expect(find('#report')).to have_text('Academic Articles')
      expect(find('#options')).to have_text('Choose citation format')

      uncheck 'include-publications'
      expect(find('#options')).not_to have_text('Choose citation format')
    end

    it 'allows user to choose different citation formats' do
      expect(find('#publications')).to have_text(
"Lyerly, AD, Namey, EE, Gray, B, Swamy, G, and Faden, RR. \"Women's views about participating in research while pregnant.\" IRB Ethics and Human Research 34, no. 4 (2012): 1-8.")

      select('MLA', from: 'citation-format-preference')
      expect(find('#publications')).to have_text(
        "Lyerly, AD, Namey, EE, Gray, B, Swamy, G, and Faden, RR. \"Women's views about participating in research while pregnant.\" IRB Ethics and Human Research 34.4 (2012): 1-8.")

      select('APA', from: 'citation-format-preference')
      expect(find('#publications')).to have_text(
        "Lyerly, AD, Namey, EE, Gray, B, Swamy, G, & Faden, RR. (2012). Women's views about participating in research while pregnant. IRB Ethics and Human Research, 34(4), 1-8.")

      select('ICMJE', from: 'citation-format-preference')
      expect(find('#publications')).to have_text(
        "Lyerly AD, Namey EE, Gray B, Swamy G, Faden RR. Women's views about participating in research while pregnant. IRB Ethics and Human Research. 2012;34(4):1-8.")
    end

    it "lets user add links back into pub" do
      expect(find('#publications')).not_to have_selector('a')

      check 'include-pub-links'
      expect(find('#publications')).to have_selector('a')
    end

  end

  context 'for person with multiple publications' do
    let(:multiple_pub_scholar) {'2845042'}

    before do
      visit_report_for(multiple_pub_scholar)
    end

    it 'displays publication sections in alphabetical order' do
      expect(page.body).to match(
        /h3.*Academic Articles.*h3.*h3.*Book Sections.*h3.*h3.*Books.*h3.*h3.*Other Articles.*h3.*h3.*Reports.*h3/)
    end

  end

  context 'for person with one award' do
    let(:one_award_winner) {'pmm21'}
    before do
      visit_report_for(one_award_winner)
    end

    xit 'displays award section', :me => true do
      expect(find('#awards')).to have_content("World's Most Average Dad. The Committee. 11/2018")
    end
  end

  context 'for person with some professional activities' do
    let(:professional_actor) {'pmm21'}
    before do
      visit_report_for(professional_actor)
    end

    xit 'displays professional activity section' do
      expect(find('#professional-activities')).to have_content("Consulting")
      expect(find('#professional-activities')).to have_content("Outreach Event")
      expect(find('#professional-activities')).to have_content("Committee Service")
      expect(find('#professional-activities')).to have_content("Event Attendance")
      expect(find('#professional-activities')).to have_content("Interview about stuff")
    end

    xit 'displays activity sections in alphabetical order' do
      expect(page.body).to match(
        /h3.*Outreach.*h3.*h3.*Presentations.*h3.*h3.*Service to Duke University.*h3.*h3.*Service to the Profession.*h3/)
    end
  end

  context 'for person with mentorship availability and overview' do
    let(:mentor) {'pmm21'}
    before do
      visit_report_for(mentor)
    end

    it 'displays mentorship section' do
      expect(find('#mentorship-availability')).to have_content("I declare myself")
      expect(find('#mentorship-availability')).to have_content("Available to mentor")
    end
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
