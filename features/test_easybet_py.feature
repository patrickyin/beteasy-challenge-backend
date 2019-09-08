Feature: Test BetEasy Horses list Generator - Patrick Yin

  Scenario Outline: Print ordered list
    Given I successfully run `/bin/bash ../../run.sh <input>`
    Then the output should contain exactly "<output>"

    Examples:
      | input | output    |
      | ../../data/Caulfield_Race1.xml ../../data/Wolferhampton_Race1.json | Advancing\nFikhaar\nToolatetodelegate\nCoronel |
      | ../../data/Caulfield_Race1.xml | Advancing\nCoronel |
      | ../../data/Wolferhampton_Race1.json | Fikhaar\nToolatetodelegate |
      | | |

  Scenario: Print nothing when no input file
    Given I successfully run `/bin/bash ../../run.sh`
        Then the output should contain exactly ""

  Scenario: Print error stacktrace when input file is not exist
    Given I successfully run `/bin/bash ../../run.sh sadasd.sss`
        Then the output should contain "FileNotFoundException"
